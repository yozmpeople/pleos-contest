package com.yozmpeople.dontspill.domain.score

// 점수 계산 입력 1건. 모든 값은 timestampMs 시점에 유효한 값이고, 모르거나 오래된 값은 null로 넘긴다.
data class DrivingScoreInput(
    // 부팅 후 경과시간(ms). SystemClock.elapsedRealtime()과 같은 기준
    val timestampMs: Long,
    // 부호 있는 차량 속도(m/s)
    val speedMps: Double? = null,
    // 현재 도로 제한속도(km/h)
    val speedLimitKmh: Double? = null,
    // 현재 위치·날짜의 일출·일몰 기준 야간 여부
    val night: Boolean? = null,
    val lowBeamOn: Boolean? = null,
    val highBeamOn: Boolean? = null,
    // 현재 위치 격자의 최근 1시간 강수량(mm)
    val rainfallMm: Double? = null,
    // 전방 와이퍼 사용 여부
    val wipersOn: Boolean? = null,
    // 실제 전륜 조향각(도). 좌회전이 음수. 핸들 회전각이 아니다.
    val frontWheelAngleDeg: Double? = null
)
