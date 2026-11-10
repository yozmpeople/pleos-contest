package com.yozmpeople.dontspill.domain.score

// 계산기가 입력 1건을 판정하기 좋게 가공한 값. 모르는 값은 null이다.
internal data class PenaltyInputs(
    // 방향을 뺀 속력(km/h)
    val speedKmh: Double? = null,
    // 유한한 양수로 확인된 제한속도(km/h)
    val speedLimitKmh: Double? = null,
    // 현재 속력 − 정확히 3초 전 속력(km/h). 감속이면 음수
    val speedChangeKmh: Double? = null,
    val night: Boolean? = null,
    // 하향등 또는 상향등이 켜져 있으면 true
    val headlightsOn: Boolean? = null,
    val highBeamOn: Boolean? = null,
    // 최근 1시간 강수량이 기준 이상이면 true
    val heavyRain: Boolean? = null,
    val wipersOn: Boolean? = null,
    // 부호 있는 횡가속도 추정값(m/s²)
    val lateralAccelMps2: Double? = null
)
