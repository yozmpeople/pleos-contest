package com.yozmpeople.dontspill.data.driving

import com.yozmpeople.dontspill.common.ApplicationScope
import com.yozmpeople.dontspill.common.ElapsedRealtimeClock
import com.yozmpeople.dontspill.common.debugLog
import com.yozmpeople.dontspill.data.driving.model.DrivingState
import com.yozmpeople.dontspill.data.vehicle.VehicleSignalRepository
import com.yozmpeople.dontspill.data.vehicle.model.Gear
import com.yozmpeople.dontspill.data.vehicle.model.IgnitionState
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

// 06 지시서 "주행 상태 기준"의 시간
private val DRIVING_CONFIRM_DURATION = 5.seconds
private val STOPPED_TIMEOUT = 5.minutes

private const val TAG = "DrivingDetector"

// 기어·차속·시동으로 주행 시작과 종료를 판단한다.
@Singleton
class DrivingDetector @Inject constructor(
    private val vehicleSignalRepository: VehicleSignalRepository,
    private val clock: ElapsedRealtimeClock,
    @ApplicationScope private val scope: CoroutineScope
) {
    private val _state = MutableStateFlow<DrivingState>(DrivingState.Idle)
    val state: StateFlow<DrivingState> = _state.asStateFlow()

    private var job: Job? = null

    // 여러 번 불러도 구독은 하나만 둔다.
    fun start() {
        if (job?.isActive == true) return
        job = scope.launch {
            // 기어·시동은 값이 안 올 수 있어서 UNKNOWN으로 시작한다. 시동은 v2.1.0 에뮬레이터에서 오지 않는다.
            combine(
                vehicleSignalRepository.gear.map { it.value }.onStart { emit(Gear.UNKNOWN) },
                vehicleSignalRepository.speedMps.map { it.value },
                vehicleSignalRepository.ignition.map { it.value }.onStart { emit(IgnitionState.UNKNOWN) },
                ::toSignal
            )
                .distinctUntilChanged()
                .collectLatest { onSignal(it) }
        }
    }

    fun stop() {
        job?.cancel()
        job = null
        moveTo(DrivingState.Idle)
    }

    // collectLatest라서 신호가 바뀌면 진행 중인 delay가 취소된다.
    private suspend fun onSignal(signal: Signal) {
        when (signal) {
            Signal.IGNITION_OFF -> moveTo(DrivingState.Idle)

            // 움직임이 5초 이어져야 주행으로 본다. 정차 중에 다시 움직이면 같은 드라이빙을 이어간다.
            Signal.MOVING -> {
                val current = _state.value
                if (current is DrivingState.Driving) return
                val movingSinceMs = clock.nowMs()
                delay(DRIVING_CONFIRM_DURATION)
                val startedAtMs = (current as? DrivingState.Stopped)?.startedAtMs ?: movingSinceMs
                moveTo(DrivingState.Driving(startedAtMs))
            }

            // 정차가 5분 이어지면 드라이빙을 끝낸다.
            // 정차 중 5초 미만으로 움직였다 멈추면 5분을 처음부터 다시 센다. 정차 시작 시각은 그대로 둔다.
            Signal.NOT_MOVING -> {
                val stopped = when (val current = _state.value) {
                    DrivingState.Idle -> return
                    is DrivingState.Driving -> DrivingState.Stopped(current.startedAtMs, clock.nowMs())
                    is DrivingState.Stopped -> current
                }
                moveTo(stopped)
                delay(STOPPED_TIMEOUT)
                moveTo(DrivingState.Idle)
            }
        }
    }

    private fun moveTo(next: DrivingState) {
        val previous = _state.value
        if (previous == next) return
        _state.value = next
        debugLog(TAG) { "$previous → $next" }
    }
}

private enum class Signal {
    IGNITION_OFF,
    MOVING,
    NOT_MOVING
}

// - 시동은 OFF·LOCK만 꺼짐으로 본다. UNKNOWN(값 없음)과 ACC는 꺼짐이 아니다.
// - 주행은 Pleos Driving 정의(기어 P 아님 + 속도 > 0)를 따른다. 기어 UNKNOWN도 P 아님이다.
// - 차속은 절댓값으로 본다. 후진이면 음수다.
private fun toSignal(gear: Gear, speedMps: Double, ignition: IgnitionState): Signal = when {
    ignition == IgnitionState.OFF || ignition == IgnitionState.LOCK -> Signal.IGNITION_OFF
    gear != Gear.PARK && abs(speedMps) > 0.0 -> Signal.MOVING
    else -> Signal.NOT_MOVING
}
