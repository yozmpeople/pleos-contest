package com.yozmpeople.dontspill.domain.score

import com.yozmpeople.dontspill.domain.score.ConditionState.MET
import com.yozmpeople.dontspill.domain.score.ConditionState.NOT_MET
import com.yozmpeople.dontspill.domain.score.ConditionState.UNKNOWN
import org.junit.Assert.assertEquals
import org.junit.Test

class PenaltyFactorTest {

    @Test
    fun `급가속과 급감속은 3초 속력 차이 30km_h를 기준으로 본다`() {
        assertEquals(MET, PenaltyFactor.RAPID_ACCELERATION.condition(PenaltyInputs(speedChangeKmh = 30.0)))
        assertEquals(NOT_MET, PenaltyFactor.RAPID_ACCELERATION.condition(PenaltyInputs(speedChangeKmh = 29.9)))
        assertEquals(MET, PenaltyFactor.RAPID_DECELERATION.condition(PenaltyInputs(speedChangeKmh = -30.0)))
        assertEquals(NOT_MET, PenaltyFactor.RAPID_DECELERATION.condition(PenaltyInputs(speedChangeKmh = 30.0)))
        assertEquals(UNKNOWN, PenaltyFactor.RAPID_ACCELERATION.condition(PenaltyInputs()))
    }

    @Test
    fun `과속은 속력과 제한속도가 모두 있어야 판정한다`() {
        val speeding = PenaltyFactor.SPEEDING

        assertEquals(MET, speeding.condition(PenaltyInputs(speedKmh = 75.0, speedLimitKmh = 60.0)))
        assertEquals(NOT_MET, speeding.condition(PenaltyInputs(speedKmh = 74.9, speedLimitKmh = 60.0)))
        assertEquals(UNKNOWN, speeding.condition(PenaltyInputs(speedKmh = 75.0)))
    }

    @Test
    fun `낮이면 전조등 상태를 몰라도 전조등 미점등이 아니다`() {
        val headlights = PenaltyFactor.HEADLIGHTS_OFF_AT_NIGHT

        assertEquals(NOT_MET, headlights.condition(PenaltyInputs(night = false)))
        assertEquals(MET, headlights.condition(PenaltyInputs(night = true, lowBeamOn = false)))
        assertEquals(UNKNOWN, headlights.condition(PenaltyInputs(night = true)))
    }

    @Test
    fun `와이퍼를 쓰고 있으면 강수량을 몰라도 와이퍼 미사용이 아니다`() {
        val wipers = PenaltyFactor.WIPERS_OFF_IN_HEAVY_RAIN

        assertEquals(NOT_MET, wipers.condition(PenaltyInputs(wipersOn = true)))
        assertEquals(MET, wipers.condition(PenaltyInputs(heavyRain = true, wipersOn = false)))
        assertEquals(UNKNOWN, wipers.condition(PenaltyInputs(heavyRain = true)))
    }

    @Test
    fun `코너링은 좌우 방향과 상관없이 횡가속도 크기로 본다`() {
        val cornering = PenaltyFactor.HARD_CORNERING

        assertEquals(MET, cornering.condition(PenaltyInputs(lateralAccelMps2 = -0.3)))
        assertEquals(NOT_MET, cornering.condition(PenaltyInputs(lateralAccelMps2 = 0.29)))
        assertEquals(UNKNOWN, cornering.condition(PenaltyInputs()))
    }

    @Test
    fun `드라이빙당 1회만 감점하는 항목은 두 번째 발생부터 감점이 0이다`() {
        assertEquals(10, PenaltyFactor.HEADLIGHTS_OFF_AT_NIGHT.penaltyFor(count = 1))
        assertEquals(0, PenaltyFactor.HEADLIGHTS_OFF_AT_NIGHT.penaltyFor(count = 2))
        assertEquals(4, PenaltyFactor.SPEEDING.penaltyFor(count = 2))
    }
}
