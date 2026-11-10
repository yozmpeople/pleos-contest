package com.yozmpeople.dontspill.domain.score

import kotlin.math.abs
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

// 안전운전 감점 항목. 조건이 requiredDuration 동안 이어지면 1회 발생으로 보고 penaltyPoints만큼 감점한다.
// oncePerDriving 항목은 횟수는 계속 세고 감점은 드라이빙당 첫 발생에만 한다.
enum class PenaltyFactor(
    val penaltyPoints: Int,
    val requiredDuration: Duration,
    val oncePerDriving: Boolean
) {
    RAPID_ACCELERATION(penaltyPoints = 2, requiredDuration = Duration.ZERO, oncePerDriving = false) {
        override fun condition(inputs: PenaltyInputs) =
            inputs.speedChangeKmh.judge { atLeast(it, RAPID_SPEED_CHANGE_KMH) }
    },
    RAPID_DECELERATION(penaltyPoints = 3, requiredDuration = Duration.ZERO, oncePerDriving = false) {
        override fun condition(inputs: PenaltyInputs) =
            inputs.speedChangeKmh.judge { atLeast(-it, RAPID_SPEED_CHANGE_KMH) }
    },
    SPEEDING(penaltyPoints = 4, requiredDuration = 6.seconds, oncePerDriving = false) {
        override fun condition(inputs: PenaltyInputs): ConditionState {
            val speedKmh = inputs.speedKmh
            val speedLimitKmh = inputs.speedLimitKmh
            if (speedKmh == null || speedLimitKmh == null) return ConditionState.UNKNOWN
            return atLeast(speedKmh - speedLimitKmh, SPEEDING_MARGIN_KMH).toConditionState()
        }
    },
    HEADLIGHTS_OFF_AT_NIGHT(penaltyPoints = 10, requiredDuration = Duration.ZERO, oncePerDriving = true) {
        override fun condition(inputs: PenaltyInputs) = isRequiredButOff(inputs.night, inputs.headlightsOn)
    },
    WIPERS_OFF_IN_HEAVY_RAIN(penaltyPoints = 10, requiredDuration = Duration.ZERO, oncePerDriving = true) {
        override fun condition(inputs: PenaltyInputs) = isRequiredButOff(inputs.heavyRain, inputs.wipersOn)
    },
    HIGH_BEAM_LEFT_ON(penaltyPoints = 7, requiredDuration = 300.seconds, oncePerDriving = false) {
        override fun condition(inputs: PenaltyInputs) = inputs.highBeamOn.toConditionState()
    },
    HARD_CORNERING(penaltyPoints = 3, requiredDuration = 2.seconds, oncePerDriving = false) {
        // 부호를 없애서 좌회전·우회전을 모두 본다.
        override fun condition(inputs: PenaltyInputs) =
            inputs.lateralAccelMps2.judge { atLeast(abs(it), HARD_CORNERING_THRESHOLD_MPS2) }
    };

    internal abstract fun condition(inputs: PenaltyInputs): ConditionState

    // count번째 발생에서 깎을 점수
    fun penaltyFor(count: Int): Int = if (!oncePerDriving || count == 1) penaltyPoints else 0
}
