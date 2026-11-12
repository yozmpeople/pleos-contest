package com.yozmpeople.dontspill.data.vehicle.model

// 차량 신호 값 1건. timestampMs는 부팅 후 경과시간(ms)으로 SystemClock.elapsedRealtime()과 같은 기준이다.
data class VehicleReading<out T>(
    val timestampMs: Long,
    val value: T
)
