package com.yozmpeople.dontspill.domain.score

import kotlin.time.Duration

// 점수(0~100), 항목별 발생 횟수, 항목별 판정 불가 누적시간
data class DrivingScoreResult(
    val score: Int,
    val counts: Map<PenaltyFactor, Int>,
    val unavailable: Map<PenaltyFactor, Duration>
)

// 감점 항목 1회가 확정된 순간. 감점이 0이면 드라이빙당 첫 발생만 감점하는 항목의 두 번째 이후 발생이다.
data class PenaltyCounted(
    val factor: PenaltyFactor,
    val timestampMs: Long,
    val count: Int,
    val deductedPoints: Int,
    val scoreAfter: Int
)
