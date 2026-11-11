package com.yozmpeople.dontspill.data.driving.local

import androidx.room3.Entity
import androidx.room3.PrimaryKey

// 드라이빙 1건의 점수 결과. 결과 화면에 필요한 값만 둔다.
// 시각은 모두 실제 시각(epoch ms)이다. elapsedRealtime은 재부팅하면 0부터 다시 시작한다.
@Entity(tableName = "driving")
data class DrivingEntity(
    @PrimaryKey val drivingId: String,
    val startedAtMs: Long,
    // 정차가 시작된 시각. 주행 중이면 null
    val endedAtMs: Long?,
    val score: Int,
    // PenaltyFactor 이름 → 횟수 JSON
    val penaltyCounts: String,
    val resultShown: Boolean
)
