package com.yozmpeople.dontspill.data.driving

import com.yozmpeople.dontspill.data.driving.local.DrivingDao
import com.yozmpeople.dontspill.data.driving.local.DrivingEntity
import com.yozmpeople.dontspill.data.driving.model.DrivingResult
import com.yozmpeople.dontspill.data.driving.model.DrivingState
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// 화면이 쓰는 주행 상태와 결과
interface DrivingRepository {
    // 주행 중이면 움직이기 시작한 시각(부팅 후 경과시간 ms), 아니면 null
    val drivingStartedAtMs: Flow<Long?>

    // 끝났지만 아직 보여 주지 않은 결과 중 가장 최근 것
    val latestUnshownResult: Flow<DrivingResult?>

    fun endDriving()

    suspend fun markResultsShown()
}

@Singleton
class DefaultDrivingRepository @Inject constructor(
    private val drivingDetector: DrivingDetector,
    private val drivingRecorder: DrivingRecorder,
    private val drivingDao: DrivingDao
) : DrivingRepository {

    override val drivingStartedAtMs: Flow<Long?> = drivingDetector.state.map {
        (it as? DrivingState.Driving)?.startedAtMs
    }

    override val latestUnshownResult: Flow<DrivingResult?> = drivingDao.observeUnshownResults().map {
        it.firstOrNull()?.toResult()
    }

    override fun endDriving() {
        drivingRecorder.endDriving()
    }

    override suspend fun markResultsShown() {
        drivingDao.markAllResultsShown()
    }
}

// observeUnshownResults는 끝난 드라이빙만 돌려주므로 endedAtMs가 있다.
private fun DrivingEntity.toResult(): DrivingResult = DrivingResult(
    drivingId = drivingId,
    character = character,
    score = score,
    durationMs = (endedAtMs ?: startedAtMs) - startedAtMs,
    distanceMeters = distanceMeters
)
