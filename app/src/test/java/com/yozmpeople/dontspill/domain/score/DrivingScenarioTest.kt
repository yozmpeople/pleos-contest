package com.yozmpeople.dontspill.domain.score

import kotlin.math.atan
import org.junit.Assert.assertEquals
import org.junit.Test

private const val WHEELBASE_M = 2.8
private const val DURATION_SECONDS = 660

// 11분 주행. 1초마다 입력 1건을 넣는다.
// 속력은 아래 지점 사이를 직선으로 잇는다. 평소 가감속은 3초에 30km/h를 넘지 않게 한다.
private val SpeedKeyframes = listOf(
    0 to 0.0,
    12 to 60.0,
    40 to 60.0,
    43 to 95.0, // 급가속 1
    60 to 95.0,
    66 to 60.0,
    80 to 60.0,
    83 to 95.0, // 급가속 2
    100 to 95.0,
    103 to 60.0, // 급감속 1
    120 to 60.0,
    127 to 95.0,
    140 to 95.0,
    143 to 60.0, // 급감속 2
    160 to 60.0,
    164 to 80.0, // 과속 1
    175 to 80.0,
    179 to 60.0,
    185 to 60.0,
    189 to 80.0, // 과속 2
    200 to 80.0,
    204 to 60.0,
    640 to 60.0,
    652 to 0.0,
    DURATION_SECONDS to 0.0
)

private const val SPEED_LIMIT_KMH = 60.0
private val SpeedLimitSeconds = 160..205

// 이 구간에서는 횡가속도가 CORNERING_MPS2가 되게 전륜각을 넣는다. 나머지는 0도다.
private const val CORNERING_MPS2 = 5.0
private val CorneringSeconds = listOf(240..243, 250..253)

private val HighBeamSeconds = listOf(0..305, 310..615)

class DrivingScenarioTest {

    private val counted = mutableListOf<PenaltyCounted>()
    private val calculator = DrivingScoreCalculator(
        startTimestampMs = 0,
        wheelbaseM = WHEELBASE_M,
        onPenaltyCounted = { counted += it }
    )

    @Test
    fun `11분 주행에서 반복 감점 항목이 두 번씩 발생하면 62점`() {
        val result = drive()

        val expected = listOf(
            PenaltyFactor.RAPID_ACCELERATION at 43,
            PenaltyFactor.RAPID_ACCELERATION at 83,
            PenaltyFactor.RAPID_DECELERATION at 103,
            PenaltyFactor.RAPID_DECELERATION at 143,
            PenaltyFactor.SPEEDING at 169,
            PenaltyFactor.SPEEDING at 194,
            PenaltyFactor.HARD_CORNERING at 242,
            PenaltyFactor.HARD_CORNERING at 252,
            PenaltyFactor.HIGH_BEAM_LEFT_ON at 300,
            PenaltyFactor.HIGH_BEAM_LEFT_ON at 610
        )
        assertEquals(expected, counted.map { it.factor at (it.timestampMs / 1_000).toInt() })
        assertEquals(100 - 2 * 2 - 3 * 2 - 4 * 2 - 3 * 2 - 7 * 2, result.score)
    }

    private fun drive(): DrivingScoreResult {
        for (second in 0..DURATION_SECONDS) {
            val speedKmh = speedAt(second)
            calculator.update(
                DrivingScoreInput(
                    timestampMs = second * 1_000L,
                    speedMps = speedKmh / 3.6,
                    speedLimitKmh = SPEED_LIMIT_KMH.takeIf { second in SpeedLimitSeconds },
                    highBeamOn = HighBeamSeconds.any { second in it },
                    frontWheelAngleDeg = if (CorneringSeconds.any { second in it }) {
                        angleFor(CORNERING_MPS2, speedKmh / 3.6)
                    } else {
                        0.0
                    }
                )
            )
        }
        return calculator.finish(DURATION_SECONDS * 1_000L)
    }

    private fun speedAt(second: Int): Double {
        val (end, endKmh) = SpeedKeyframes.first { (keySecond, _) -> keySecond >= second }
        val (start, startKmh) = SpeedKeyframes.last { (keySecond, _) -> keySecond <= second }
        if (start == end) return startKmh
        return startKmh + (endKmh - startKmh) * (second - start) / (end - start)
    }

    private fun angleFor(lateralAccelMps2: Double, speedMps: Double): Double =
        Math.toDegrees(atan(lateralAccelMps2 * WHEELBASE_M / (speedMps * speedMps)))

    private infix fun PenaltyFactor.at(second: Int) = this to second
}
