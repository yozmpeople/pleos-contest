package com.yozmpeople.dontspill.data.driving

import com.yozmpeople.dontspill.common.ApplicationScope
import com.yozmpeople.dontspill.common.ElapsedRealtimeClock
import com.yozmpeople.dontspill.common.WallClock
import com.yozmpeople.dontspill.common.debugLog
import com.yozmpeople.dontspill.data.driving.local.DrivingDao
import com.yozmpeople.dontspill.data.driving.local.DrivingEntity
import com.yozmpeople.dontspill.data.driving.model.DrivingState
import com.yozmpeople.dontspill.data.vehicle.VehicleSignalRepository
import com.yozmpeople.dontspill.data.vehicle.model.VehicleReading
import com.yozmpeople.dontspill.domain.score.DrivingScoreCalculator
import com.yozmpeople.dontspill.domain.score.DrivingScoreInput
import com.yozmpeople.dontspill.domain.score.DrivingScoreResult
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

private const val TAG = "DrivingRecorder"

// 주행 상태에 따라 드라이빙을 시작·종료하고, 주행 중 점수를 계산해 Room에 저장한다.
@Singleton
class DrivingRecorder @Inject constructor(
    private val drivingDetector: DrivingDetector,
    private val vehicleSignalRepository: VehicleSignalRepository,
    private val drivingDao: DrivingDao,
    private val clock: ElapsedRealtimeClock,
    private val wallClock: WallClock,
    @ApplicationScope private val scope: CoroutineScope
) {
    private var job: Job? = null

    // 여러 번 불러도 기록은 하나만 둔다. 감지도 같이 시작한다.
    fun start() {
        if (job?.isActive == true) return
        drivingDetector.start()
        job = scope.launch {
            // collectLatest라서 주행이 대기로 바뀌면 진행 중인 기록이 취소되고, finally에서 드라이빙을 끝낸다.
            drivingDetector.state.collectLatest { state ->
                if (state is DrivingState.Driving) record(state)
            }
        }
    }

    // 주행 중이면 지금 시각으로 드라이빙을 끝낸다.
    fun stop() {
        job?.cancel()
        job = null
        drivingDetector.stop()
    }

    private suspend fun record(driving: DrivingState.Driving) {
        val drivingId = UUID.randomUUID().toString()
        val calculator = DrivingScoreCalculator(driving.startedAtMs, vehicleSignalRepository.readWheelbaseM())
        try {
            val initial = calculator.result()
            drivingDao.insert(
                DrivingEntity(
                    drivingId = drivingId,
                    startedAtMs = toEpochMs(driving.startedAtMs),
                    endedAtMs = null,
                    score = initial.score,
                    penaltyCounts = encodeCounts(initial),
                    resultShown = false
                )
            )
            debugLog(TAG) { "드라이빙 시작" }
            collectInputs(driving.startedAtMs, calculator, initial) { result ->
                drivingDao.updateScore(drivingId, result.score, encodeCounts(result))
            }
        } finally {
            // 대기로 바뀐 시각(P 진입, 시동 OFF)을 종료 시각으로 쓴다.
            val endedAtMs = clock.nowMs()
            val result = calculator.finish(endedAtMs)
            withContext(NonCancellable) {
                drivingDao.finish(drivingId, toEpochMs(endedAtMs), result.score, encodeCounts(result))
            }
            debugLog(TAG) { "드라이빙 종료: 점수 ${result.score}" }
        }
    }

    // 차속 이벤트마다 나머지 신호의 최신 값으로 입력 1건을 넣는다 (05 지시서 "수집 주기")
    // 점수와 횟수는 감점이 확정될 때만 바뀌므로, 횟수가 바뀌었을 때만 저장한다.
    private suspend fun collectInputs(
        startedAtMs: Long,
        calculator: DrivingScoreCalculator,
        initial: DrivingScoreResult,
        onCountsChanged: suspend (DrivingScoreResult) -> Unit
    ): Unit = coroutineScope {
        val speedLimitKmh = vehicleSignalRepository.speedLimitKmh.latestIn(this)
        val lowBeamOn = vehicleSignalRepository.lowBeamOn.latestIn(this)
        val highBeamOn = vehicleSignalRepository.highBeamOn.latestIn(this)
        val wipersOn = vehicleSignalRepository.frontWipersOn.latestIn(this)
        val frontWheelAngleDeg = vehicleSignalRepository.frontWheelAngleDeg.latestIn(this)

        var lastTimestampMs = startedAtMs
        var savedCounts = initial.counts
        vehicleSignalRepository.speedMps.collect { speed ->
            // 계산기는 시간순 입력을 전제로 한다. 시작 전 값(replay)과 순서가 뒤바뀐 값은 넣지 않는다.
            if (speed.timestampMs <= lastTimestampMs) return@collect
            lastTimestampMs = speed.timestampMs

            // night, rainfallMm은 출몰시각·강수량 API를 연결하면 채운다. 그 전에는 null(판정 불가)이다.
            val result = calculator.update(
                DrivingScoreInput(
                    timestampMs = speed.timestampMs,
                    speedMps = speed.value,
                    speedLimitKmh = speedLimitKmh.value,
                    lowBeamOn = lowBeamOn.value,
                    highBeamOn = highBeamOn.value,
                    wipersOn = wipersOn.value,
                    frontWheelAngleDeg = frontWheelAngleDeg.value
                )
            )
            if (result.counts != savedCounts) {
                savedCounts = result.counts
                onCountsChanged(result)
            }
        }
    }

    // 부팅 후 경과시간을 실제 시각으로 바꾼다. 재부팅하면 경과시간이 0부터 다시 시작해서 저장은 실제 시각으로 한다.
    private fun toEpochMs(elapsedRealtimeMs: Long): Long = wallClock.nowMs() - (clock.nowMs() - elapsedRealtimeMs)
}

private fun <T> Flow<VehicleReading<T?>>.latestIn(scope: CoroutineScope): StateFlow<T?> =
    map { it.value }.stateIn(scope, SharingStarted.Eagerly, null)

// PenaltyFactor 이름 → 횟수 JSON
private fun encodeCounts(result: DrivingScoreResult): String =
    Json.encodeToString(result.counts.mapKeys { it.key.name })
