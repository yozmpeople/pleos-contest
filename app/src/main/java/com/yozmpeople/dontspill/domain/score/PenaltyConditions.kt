package com.yozmpeople.dontspill.domain.score

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.tan

private const val RELATIVE_TOLERANCE = 1e-12

internal const val RAPID_SPEED_CHANGE_KMH = 30.0
internal const val SPEEDING_MARGIN_KMH = 15.0

internal const val HEAVY_RAIN_MM = 5.0

internal const val HARD_CORNERING_THRESHOLD_MPS2 = 3.0

// 단위 환산·보간·삼각함수에서 생기는 부동소수점 오차만 같은 값으로 본다. 판정용 여유 임계값이 아니다.
internal fun atLeast(value: Double, threshold: Double): Boolean =
    value >= threshold || abs(value - threshold) <= RELATIVE_TOLERANCE * max(abs(value), abs(threshold))

// 장치가 필요한 상황인데 꺼져 있으면 MET. 필요 없거나 켜져 있으면 NOT_MET, 결론을 못 내면 UNKNOWN
internal fun isRequiredButOff(required: Boolean?, inUse: Boolean?): ConditionState = when {
    required == false || inUse == true -> ConditionState.NOT_MET
    required == true && inUse == false -> ConditionState.MET
    else -> ConditionState.UNKNOWN
}

// 속도·전륜각·축거로 구한 무미끄럼 단선 모델의 부호 있는 횡가속도(m/s²). 입력이 유효하지 않으면 null
internal fun estimateLateralAccel(speedMps: Double?, frontWheelAngleDeg: Double?, wheelbaseM: Double?): Double? {
    if (speedMps == null || frontWheelAngleDeg == null || wheelbaseM == null) return null
    if (!speedMps.isFinite() || !frontWheelAngleDeg.isFinite() || !wheelbaseM.isFinite()) return null
    if (wheelbaseM <= 0 || frontWheelAngleDeg <= -90 || frontWheelAngleDeg >= 90) return null
    // 타이어 미끄럼·언더스티어·후륜 조향은 무시한다.
    val estimate = speedMps * speedMps * tan(Math.toRadians(frontWheelAngleDeg)) / wheelbaseM
    return estimate.takeIf { it.isFinite() }
}
