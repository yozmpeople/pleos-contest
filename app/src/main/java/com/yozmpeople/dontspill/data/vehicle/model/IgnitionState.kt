package com.yozmpeople.dontspill.data.vehicle.model

// 앱에서 쓰는 시동 상태. AAOS VehicleIgnitionState와 같은 단계다.
enum class IgnitionState {
    LOCK,
    OFF,
    ACC,
    ON,
    START,
    UNKNOWN
}
