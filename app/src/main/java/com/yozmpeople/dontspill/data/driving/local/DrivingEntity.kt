package com.yozmpeople.dontspill.data.driving.local

import androidx.room3.Entity
import androidx.room3.PrimaryKey

// 드라이빙 1건의 점수 결과. 결과 화면에 필요한 값만 둔다.
// 시각은 모두 실제 시각(epoch ms)이다. elapsedRealtime은 재부팅하면 0부터 다시 시작한다.
@Entity(tableName = "driving")
data class DrivingEntity(
    @PrimaryKey val drivingId: String,
    val startedAtMs: Long,
    // P 진입 또는 시동 OFF 시각. 주행 중이면 null
    val endedAtMs: Long?,
    val score: Int,
    // 드라이빙을 시작할 때 고른 캐릭터의 enum 이름
    val character: String,
    // 위치 점 사이 거리를 더한 값(m). 드라이빙이 끝날 때 넣는다.
    val distanceMeters: Double,
    // PenaltyFactor 이름 → 횟수 JSON
    val penaltyCounts: String,
    val resultShown: Boolean
)
