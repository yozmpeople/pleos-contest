package com.yozmpeople.dontspill.domain.score

import kotlin.math.abs
import kotlin.math.max
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

private const val START_SCORE = 100
private const val MPS_TO_KMH = 3.6
private const val MAX_VALID_RAINFALL_MM = 900.0
private val SPEED_CHANGE_WINDOW_MS = 3.seconds.inWholeMilliseconds

/**
 * 드라이빙 1건의 안전운전 점수를 계산한다. 드라이빙이 시작되면 만들고, 입력마다 [update],
 * 드라이빙이 끝나면 [finish]를 부른다. 유지 시간과 정상 복귀는 입력이 올 때만 확인하므로 1초에 한 번 이상 넣는다.
 *
 * 스레드 안전하지 않다. 한 스레드(또는 하나의 코루틴)에서 시각 순서대로 호출한다.
 */
class DrivingScoreCalculator(
    startTimestampMs: Long,
    private val wheelbaseM: Double?,
    private val onPenaltyCounted: (PenaltyCounted) -> Unit = {}
) {

    private class SpeedSample(
        val timestampMs: Long,
        val speedKmh: Double
    )

    private var score = START_SCORE
    private var lastTimestampMs = startTimestampMs
    private var finished = false
    private val speedHistory = ArrayDeque<SpeedSample>()
    private val trackers = PenaltyFactor.entries.associateWith { PenaltyTracker(it) }

    fun update(input: DrivingScoreInput): DrivingScoreResult {
        if (finished) return result()

        accountUntil(input.timestampMs)
        val inputs = toPenaltyInputs(input)
        for ((factor, tracker) in trackers) {
            if (!tracker.record(factor.condition(inputs), input.timestampMs)) continue
            val deductedPoints = factor.penaltyFor(tracker.count)
            score = max(0, score - deductedPoints)
            onPenaltyCounted(PenaltyCounted(factor, input.timestampMs, tracker.count, deductedPoints, score))
        }
        return result()
    }

    // 종료 시각까지의 판정 불가 시간만 더한다. 덜 채운 조건은 감점하지 않는다. 종료 뒤 입력은 무시한다.
    fun finish(endTimestampMs: Long): DrivingScoreResult {
        if (finished) return result()
        accountUntil(endTimestampMs)
        finished = true
        return result()
    }

    fun result(): DrivingScoreResult = DrivingScoreResult(
        score = score,
        counts = trackers.mapValues { (_, tracker) -> tracker.count },
        unavailable = trackers.mapValues { (_, tracker) -> tracker.unavailable }
    )

    // 직전 판정이 UNKNOWN이던 항목에 이번 시각까지의 경과시간을 더한다. 새 판정을 기록하기 전에 부른다.
    private fun accountUntil(timestampMs: Long) {
        // 입력은 시간순으로 온다고 본다.
        val elapsed = (timestampMs - lastTimestampMs).milliseconds
        trackers.values.forEach { it.accountElapsed(elapsed) }
        lastTimestampMs = timestampMs
    }

    private fun toPenaltyInputs(input: DrivingScoreInput): PenaltyInputs {
        val speedKmh = input.speedMps?.let { abs(it) * MPS_TO_KMH }?.takeIf { it.isFinite() }
        return PenaltyInputs(
            speedKmh = speedKmh,
            speedLimitKmh = input.speedLimitKmh?.takeIf { it.isFinite() && it > 0 },
            speedChangeKmh = speedChange(input.timestampMs, speedKmh),
            night = input.night,
            lowBeamOn = input.lowBeamOn,
            highBeamOn = input.highBeamOn,
            heavyRain = input.rainfallMm
                ?.takeIf { it.isFinite() && it >= 0 && it < MAX_VALID_RAINFALL_MM }
                ?.let { atLeast(it, HEAVY_RAIN_MM) },
            wipersOn = input.wipersOn,
            lateralAccelMps2 = estimateLateralAccel(input.speedMps, input.frontWheelAngleDeg, wheelbaseM)
        )
    }

    // 현재 속력 − 정확히 3초 전 속력(km/h). 3초 이력이 없거나 속력을 모르면 null
    private fun speedChange(timestampMs: Long, speedKmh: Double?): Double? {
        if (speedKmh == null) {
            speedHistory.clear()
            return null
        }
        speedHistory.addLast(SpeedSample(timestampMs, speedKmh))
        val cutoffMs = timestampMs - SPEED_CHANGE_WINDOW_MS
        while (speedHistory.size > 1 && speedHistory[1].timestampMs <= cutoffMs) {
            speedHistory.removeFirst()
        }
        val before = speedHistory.first()
        if (before.timestampMs > cutoffMs) return null
        if (before.timestampMs == cutoffMs) return speedKmh - before.speedKmh

        // 표본 사이의 속도는 선형으로 본다.
        val after = speedHistory[1]
        val fraction = (cutoffMs - before.timestampMs).toDouble() / (after.timestampMs - before.timestampMs)
        val speedAtCutoffKmh = before.speedKmh + (after.speedKmh - before.speedKmh) * fraction
        return speedKmh - speedAtCutoffKmh
    }
}
