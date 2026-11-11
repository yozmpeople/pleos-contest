package com.yozmpeople.dontspill.data.location.model

// 위치 1건. 로그에 남기지 않는다.
// timestampMs는 SDK의 timestampMillis를 그대로 쓴다. 실제 시각(epoch)인지 부팅 후 경과시간인지 미확인이다.
data class Location(
    val latitudeDeg: Double,
    val longitudeDeg: Double,
    val timestampMs: Long
)
