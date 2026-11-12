package com.yozmpeople.dontspill.domain.score

import com.yozmpeople.dontspill.domain.score.ConditionState.MET
import com.yozmpeople.dontspill.domain.score.ConditionState.NOT_MET
import com.yozmpeople.dontspill.domain.score.ConditionState.UNKNOWN
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

// 과속(유지 6초)으로 공통 규칙을 확인한다.
class PenaltyTrackerTest {

    private val tracker = PenaltyTracker(PenaltyFactor.SPEEDING)

    @Test
    fun `조건이 유지 시간을 채운 입력에서 1회가 확정된다`() {
        assertFalse(tracker.record(MET, 0))
        assertFalse(tracker.record(MET, 5_999))
        assertTrue(tracker.record(MET, 6_000))
        assertEquals(1, tracker.count)
    }

    @Test
    fun `이미 센 구간은 조건이 이어져도 다시 세지 않는다`() {
        tracker.record(MET, 0)
        tracker.record(MET, 6_000)

        assertFalse(tracker.record(MET, 20_000))
        assertEquals(1, tracker.count)
    }

    @Test
    fun `정상이 1초 이어진 것을 확인하면 다시 셀 수 있다`() {
        tracker.record(MET, 0)
        tracker.record(MET, 6_000)
        tracker.record(NOT_MET, 7_000)
        tracker.record(NOT_MET, 8_000)
        tracker.record(MET, 9_000)

        assertTrue(tracker.record(MET, 15_000))
        assertEquals(2, tracker.count)
    }

    @Test
    fun `정상이 1초 안 되고 다시 조건이 오면 같은 구간이다`() {
        tracker.record(MET, 0)
        tracker.record(MET, 6_000)
        tracker.record(NOT_MET, 7_000)
        tracker.record(NOT_MET, 7_999)
        tracker.record(MET, 8_000)

        assertFalse(tracker.record(MET, 20_000))
        assertEquals(1, tracker.count)
    }

    @Test
    fun `판정 불가가 오면 진행 중인 유지 시간을 처음부터 다시 잰다`() {
        tracker.record(MET, 0)
        tracker.record(MET, 5_000)
        tracker.record(UNKNOWN, 5_500)
        tracker.record(MET, 6_000)

        assertFalse(tracker.record(MET, 11_999))
        assertTrue(tracker.record(MET, 12_000))
    }

    @Test
    fun `판정 불가로는 이미 센 표시가 풀리지 않는다`() {
        tracker.record(MET, 0)
        tracker.record(MET, 6_000)
        tracker.record(UNKNOWN, 7_000)
        tracker.record(MET, 8_000)

        assertFalse(tracker.record(MET, 20_000))
        assertEquals(1, tracker.count)
    }

    @Test
    fun `직전 판정이 판정 불가였던 시간만 더한다`() {
        // 처음은 판정 불가 상태다.
        tracker.accountElapsed(2.seconds)
        tracker.record(MET, 2_000)
        tracker.accountElapsed(1.seconds)
        tracker.record(UNKNOWN, 3_000)
        tracker.accountElapsed(500.milliseconds)
        tracker.record(NOT_MET, 3_500)

        assertEquals(2.5.seconds, tracker.unavailable)
    }

    @Test
    fun `처음 만들면 횟수와 판정 불가 시간이 0이다`() {
        assertEquals(0, tracker.count)
        assertEquals(Duration.ZERO, tracker.unavailable)
    }
}
