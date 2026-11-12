package com.yozmpeople.dontspill.domain.score

import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

// 정상 상태가 이만큼 이어진 것을 확인해야 같은 항목을 다시 셀 수 있다.
private val RECOVERY_DURATION = 1.seconds

// 감점 항목 1개의 발생 횟수와 판정 불가 시간을 관리한다.
internal class PenaltyTracker(private val factor: PenaltyFactor) {

    var count = 0
        private set
    var unavailable: Duration = Duration.ZERO
        private set

    // 지금 이어지는 구간에서 이미 횟수를 셌으면 true. 정상 복귀가 확인돼야 풀리고, UNKNOWN으로는 풀리지 않는다.
    private var counted = false
    private var metSinceMs: Long? = null
    private var notMetSinceMs: Long? = null
    private var lastCondition = ConditionState.UNKNOWN

    // 직전 판정이 UNKNOWN이었으면 그 뒤로 지난 시간을 판정 불가 시간에 더한다.
    fun accountElapsed(elapsed: Duration) {
        if (lastCondition == ConditionState.UNKNOWN) unavailable += elapsed
    }

    // 이번 판정을 기록한다. 이번 입력에서 1회가 확정되면 true
    fun record(condition: ConditionState, timestampMs: Long): Boolean {
        lastCondition = condition
        when (condition) {
            ConditionState.UNKNOWN -> {
                metSinceMs = null
                notMetSinceMs = null
            }

            ConditionState.NOT_MET -> {
                metSinceMs = null
                val sinceMs = notMetSinceMs ?: timestampMs
                notMetSinceMs = sinceMs
                if ((timestampMs - sinceMs).milliseconds >= RECOVERY_DURATION) counted = false
            }

            ConditionState.MET -> {
                notMetSinceMs = null
                val sinceMs = metSinceMs ?: timestampMs
                metSinceMs = sinceMs
                if (counted || (timestampMs - sinceMs).milliseconds < factor.requiredDuration) return false
                count += 1
                counted = true
                return true
            }
        }
        return false
    }
}
