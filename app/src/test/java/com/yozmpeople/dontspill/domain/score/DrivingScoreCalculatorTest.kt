package com.yozmpeople.dontspill.domain.score

import kotlin.math.atan
import kotlin.math.roundToLong
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import org.junit.Assert.assertEquals
import org.junit.Test

private const val WHEELBASE_M = 2.8
private const val CORNERING_SPEED_MPS = 10.0

class DrivingScoreCalculatorTest {

    private val calculator = DrivingScoreCalculator(startTimestampMs = 0, wheelbaseM = WHEELBASE_M)

    @Test
    fun `3초 동안 60에서 90km_h로 가속하면 급가속 1회 98점`() {
        calculator.update(input(0.0, speedKmh = 60.0))
        val result = calculator.update(input(3.0, speedKmh = 90.0))

        assertOccurred(result, PenaltyFactor.RAPID_ACCELERATION, count = 1, score = 98)
    }

    @Test
    fun `3초 동안 90에서 60km_h로 감속하면 급감속 1회 97점`() {
        calculator.update(input(0.0, speedKmh = 90.0))
        val result = calculator.update(input(3.0, speedKmh = 60.0))

        assertOccurred(result, PenaltyFactor.RAPID_DECELERATION, count = 1, score = 97)
    }

    @Test
    fun `제한속도 60에서 75km_h가 5_999초 이어지면 과속이 아직 아니다`() {
        val seconds = listOf(0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 5.999)
        val result = seconds.map { calculator.update(input(it, speedKmh = 75.0, speedLimitKmh = 60.0)) }.last()

        assertOccurred(result, PenaltyFactor.SPEEDING, count = 0, score = 100)
    }

    @Test
    fun `같은 과속 조건이 6초 이어지면 과속 1회 96점`() {
        val result = (0..6).map { calculator.update(input(it.toDouble(), speedKmh = 75.0, speedLimitKmh = 60.0)) }
            .last()

        assertOccurred(result, PenaltyFactor.SPEEDING, count = 1, score = 96)
    }

    @Test
    fun `야간 전조등 미점등이 1초 정상 복귀 뒤 다시 나오면 2회지만 10점만 감점`() {
        calculator.update(input(0.0, night = true, lowBeamOn = false, highBeamOn = false))
        calculator.update(input(1.0, night = true, lowBeamOn = true, highBeamOn = false))
        calculator.update(input(2.0, night = true, lowBeamOn = true, highBeamOn = false))
        val result = calculator.update(input(3.0, night = true, lowBeamOn = false, highBeamOn = false))

        assertOccurred(result, PenaltyFactor.HEADLIGHTS_OFF_AT_NIGHT, count = 2, score = 90)
    }

    @Test
    fun `강수량 10mm에서 와이퍼를 쓰지 않으면 첫 발생 즉시 90점`() {
        val result = calculator.update(input(0.0, rainfallMm = 10.0, wipersOn = false))

        assertOccurred(result, PenaltyFactor.WIPERS_OFF_IN_HEAVY_RAIN, count = 1, score = 90)
    }

    @Test
    fun `상향등이 299_999초 켜져 있으면 아직 발생하지 않는다`() {
        calculator.update(input(0.0, highBeamOn = true))
        val result = calculator.update(input(299.999, highBeamOn = true))

        assertOccurred(result, PenaltyFactor.HIGH_BEAM_LEFT_ON, count = 0, score = 100)
    }

    @Test
    fun `상향등이 300초 켜지면 1회 93점이고 계속 켜도 다시 감점하지 않는다`() {
        calculator.update(input(0.0, highBeamOn = true))
        val atLimit = calculator.update(input(300.0, highBeamOn = true))
        val later = calculator.update(input(600.0, highBeamOn = true))

        assertOccurred(atLimit, PenaltyFactor.HIGH_BEAM_LEFT_ON, count = 1, score = 93)
        assertOccurred(later, PenaltyFactor.HIGH_BEAM_LEFT_ON, count = 1, score = 93)
    }

    @Test
    fun `상향등을 1초 끈 것이 확인된 뒤 다시 300초 켜면 2회 86점`() {
        calculator.update(input(0.0, highBeamOn = true))
        calculator.update(input(300.0, highBeamOn = true))
        calculator.update(input(301.0, highBeamOn = false))
        calculator.update(input(302.0, highBeamOn = false))
        calculator.update(input(303.0, highBeamOn = true))
        val result = calculator.update(input(603.0, highBeamOn = true))

        assertOccurred(result, PenaltyFactor.HIGH_BEAM_LEFT_ON, count = 2, score = 86)
    }

    @Test
    fun `횡가속 추정값 -0_3이 2초 이어지면 무리한 코너링 1회 97점`() {
        val result = (0..2).map { calculator.update(cornering(it.toDouble(), lateralAccelMps2 = -0.3)) }.last()

        assertOccurred(result, PenaltyFactor.HARD_CORNERING, count = 1, score = 97)
    }

    @Test
    fun `전륜각이 null이면 코너링은 판정 불가이고 진행 중인 2초 확인을 처음부터 다시 잰다`() {
        calculator.update(cornering(0.0, lateralAccelMps2 = 0.5))
        calculator.update(cornering(1.5, lateralAccelMps2 = 0.5))
        calculator.update(input(1.8, speedKmh = CORNERING_SPEED_MPS * 3.6, frontWheelAngleDeg = null))
        val result = calculator.update(cornering(3.0, lateralAccelMps2 = 0.5))

        assertOccurred(result, PenaltyFactor.HARD_CORNERING, count = 0, score = 100)
        // 1.8초 판정 불가부터 3초 입력까지
        assertEquals(1200.milliseconds, result.unavailable.getValue(PenaltyFactor.HARD_CORNERING))
    }

    @Test
    fun `코너링 1초 뒤 판정 불가 뒤 다시 1초면 합산하지 않는다`() {
        calculator.update(cornering(0.0, lateralAccelMps2 = 0.5))
        calculator.update(cornering(1.0, lateralAccelMps2 = 0.5))
        calculator.update(input(1.5, speedKmh = CORNERING_SPEED_MPS * 3.6))
        calculator.update(cornering(2.0, lateralAccelMps2 = 0.5))
        val result = calculator.update(cornering(3.0, lateralAccelMps2 = 0.5))

        assertOccurred(result, PenaltyFactor.HARD_CORNERING, count = 0, score = 100)
    }

    @Test
    fun `이미 센 코너링은 판정 불가를 지나 같은 조건이 다시 와도 정상 복귀 전에는 세지 않는다`() {
        (0..2).forEach { calculator.update(cornering(it.toDouble(), lateralAccelMps2 = 0.5)) }
        calculator.update(input(3.0, speedKmh = CORNERING_SPEED_MPS * 3.6))
        val result = (4..6).map { calculator.update(cornering(it.toDouble(), lateralAccelMps2 = 0.5)) }.last()

        assertOccurred(result, PenaltyFactor.HARD_CORNERING, count = 1, score = 97)
    }

    @Test
    fun `점수가 0점이어도 발생 횟수는 계속 센다`() {
        // 8초 주기로 0km/h 4초, 40km/h 4초를 반복한다. 주기마다 급가속 1회(2점), 급감속 1회(3점)
        var result = calculator.result()
        for (second in 0 until 200) {
            val speedKmh = if (second % 8 < 4) 0.0 else 40.0
            result = calculator.update(input(second.toDouble(), speedKmh = speedKmh))
        }

        assertEquals(0, result.score)
        assertEquals(25, result.counts.getValue(PenaltyFactor.RAPID_ACCELERATION))
        assertEquals(24, result.counts.getValue(PenaltyFactor.RAPID_DECELERATION))
    }

    @Test
    fun `일곱 항목이 한 번씩 발생하면 61점`() {
        val corneringAngle = angleFor(lateralAccelMps2 = 0.5, speedMps = 60.0 / 3.6)
        var result = calculator.result()
        for (second in 0..60) {
            result = calculator.update(
                DrivingScoreInput(
                    timestampMs = millis(second.toDouble()),
                    speedMps = (if (second < 10 || second >= 40) 60.0 else 90.0) / 3.6,
                    speedLimitKmh = if (second in 20..26) 70.0 else null,
                    night = second == 0,
                    lowBeamOn = false,
                    highBeamOn = second in 1..301,
                    rainfallMm = 10.0,
                    wipersOn = second > 0,
                    frontWheelAngleDeg = if (second in 50..52) corneringAngle else 0.0
                )
            )
        }
        for (second in 61..301) {
            result = calculator.update(input(second.toDouble(), highBeamOn = true))
        }

        assertEquals(61, result.score)
        PenaltyFactor.entries.forEach { assertEquals(it.name, 1, result.counts.getValue(it)) }
    }

    @Test
    fun `판정 불가 시간은 첫 입력 전과 3초 이력이 없는 시간과 종료까지를 더한다`() {
        (1..4).forEach { calculator.update(input(it.toDouble(), speedKmh = 60.0)) }
        val result = calculator.finish(millis(6.0))

        // 급가속은 4초 입력부터 판정할 수 있다. 0~4초가 판정 불가다.
        assertEquals(4.seconds, result.unavailable.getValue(PenaltyFactor.RAPID_ACCELERATION))
        // 제한속도가 없어서 과속은 시작부터 종료까지 판정 불가다.
        assertEquals(6.seconds, result.unavailable.getValue(PenaltyFactor.SPEEDING))
    }

    @Test
    fun `3초 전 시각이 두 표본 사이면 선형 보간한 속력과 비교한다`() {
        calculator.update(input(0.0, speedKmh = 60.0))
        calculator.update(input(1.0, speedKmh = 70.0))
        // 0.5초 시점 속력은 65km/h로 보간된다. 95 − 65 = 30
        val result = calculator.update(input(3.5, speedKmh = 95.0))

        assertOccurred(result, PenaltyFactor.RAPID_ACCELERATION, count = 1, score = 98)
    }

    @Test
    fun `속력이 미확인이면 이력을 비워서 그 앞의 속력과 비교하지 않는다`() {
        calculator.update(input(0.0, speedKmh = 60.0))
        calculator.update(input(1.0, speedKmh = null))
        val result = calculator.update(input(3.0, speedKmh = 90.0))

        assertOccurred(result, PenaltyFactor.RAPID_ACCELERATION, count = 0, score = 100)
    }

    @Test
    fun `반환한 결과는 이후 update로 바뀌지 않는다`() {
        val before = calculator.update(input(0.0, rainfallMm = 5.0, wipersOn = false))
        calculator.update(input(1.0, rainfallMm = 10.0, wipersOn = false))

        assertEquals(100, before.score)
        assertEquals(0, before.counts.getValue(PenaltyFactor.WIPERS_OFF_IN_HEAVY_RAIN))
    }

    @Test
    fun `항목이 1회 확정될 때마다 알림이 온다`() {
        val events = mutableListOf<PenaltyCounted>()
        val calculator = DrivingScoreCalculator(startTimestampMs = 0, wheelbaseM = WHEELBASE_M) { events += it }
        calculator.update(input(0.0, night = true, lowBeamOn = false, highBeamOn = false))
        calculator.update(input(1.0, night = true, lowBeamOn = true, highBeamOn = false))
        calculator.update(input(2.0, night = true, lowBeamOn = true, highBeamOn = false))
        calculator.update(input(3.0, night = true, lowBeamOn = false, highBeamOn = false))

        assertEquals(
            listOf(
                headlightsCounted(timestampMs = 0, count = 1, deductedPoints = 10),
                headlightsCounted(timestampMs = 3_000, count = 2, deductedPoints = 0)
            ),
            events
        )
    }

    @Test
    fun `종료 뒤에 들어온 입력은 계산하지 않는다`() {
        calculator.update(input(0.0, speedKmh = 60.0))
        val finished = calculator.finish(millis(2.0))
        val afterFinish = calculator.update(input(3.0, speedKmh = 90.0))
        val finishedAgain = calculator.finish(millis(10.0))

        assertEquals(finished, afterFinish)
        assertEquals(finished, finishedAgain)
    }

    private fun headlightsCounted(timestampMs: Long, count: Int, deductedPoints: Int) = PenaltyCounted(
        factor = PenaltyFactor.HEADLIGHTS_OFF_AT_NIGHT,
        timestampMs = timestampMs,
        count = count,
        deductedPoints = deductedPoints,
        scoreAfter = 90
    )

    private fun input(
        second: Double,
        speedKmh: Double? = null,
        speedLimitKmh: Double? = null,
        night: Boolean? = null,
        lowBeamOn: Boolean? = null,
        highBeamOn: Boolean? = null,
        rainfallMm: Double? = null,
        wipersOn: Boolean? = null,
        frontWheelAngleDeg: Double? = null
    ) = DrivingScoreInput(
        timestampMs = millis(second),
        speedMps = speedKmh?.let { it / 3.6 },
        speedLimitKmh = speedLimitKmh,
        night = night,
        lowBeamOn = lowBeamOn,
        highBeamOn = highBeamOn,
        rainfallMm = rainfallMm,
        wipersOn = wipersOn,
        frontWheelAngleDeg = frontWheelAngleDeg
    )

    // 일정한 속도로 돌면서 횡가속도 추정값이 lateralAccelMps2가 되는 입력
    private fun cornering(second: Double, lateralAccelMps2: Double) = input(
        second,
        speedKmh = CORNERING_SPEED_MPS * 3.6,
        frontWheelAngleDeg = angleFor(lateralAccelMps2, CORNERING_SPEED_MPS)
    )

    private fun angleFor(lateralAccelMps2: Double, speedMps: Double): Double =
        Math.toDegrees(atan(lateralAccelMps2 * WHEELBASE_M / (speedMps * speedMps)))

    private fun millis(second: Double): Long = (second * 1_000).roundToLong()

    private fun assertOccurred(result: DrivingScoreResult, factor: PenaltyFactor, count: Int, score: Int) {
        assertEquals(count, result.counts.getValue(factor))
        assertEquals(score, result.score)
    }
}
