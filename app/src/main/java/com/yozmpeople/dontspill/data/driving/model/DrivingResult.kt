package com.yozmpeople.dontspill.data.driving.model

// 끝난 드라이빙 1건의 결과. 결과 화면에 필요한 값만 둔다.
data class DrivingResult(
    val drivingId: String,
    // 드라이빙을 시작할 때 고른 캐릭터의 enum 이름
    val character: String,
    val score: Int,
    val durationMs: Long,
    val distanceMeters: Double
)
