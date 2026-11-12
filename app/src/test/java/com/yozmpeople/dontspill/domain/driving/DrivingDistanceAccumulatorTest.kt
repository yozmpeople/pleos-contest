package com.yozmpeople.dontspill.domain.driving

import org.junit.Assert.assertEquals
import org.junit.Test

class DrivingDistanceAccumulatorTest {

    @Test
    fun `점이 하나면 거리는 0이다`() {
        val accumulator = DrivingDistanceAccumulator()

        accumulator.add(37.5665, 126.9780)

        assertEquals(0.0, accumulator.totalMeters, 0.0)
    }

    @Test
    fun `경도 1도는 적도에서 약 111km다`() {
        assertEquals(111_195.0, distanceMeters(0.0, 0.0, 0.0, 1.0), 1.0)
    }

    @Test
    fun `서울시청에서 부산시청까지 직선거리는 약 325km다`() {
        assertEquals(325_000.0, distanceMeters(37.5665, 126.9780, 35.1796, 129.0756), 2_000.0)
    }

    @Test
    fun `점 사이 거리를 이어서 더한다`() {
        val accumulator = DrivingDistanceAccumulator()

        accumulator.add(0.0, 0.0)
        accumulator.add(0.0, 1.0)
        accumulator.add(0.0, 2.0)

        assertEquals(222_390.0, accumulator.totalMeters, 2.0)
    }

    @Test
    fun `같은 자리에 머물면 거리가 늘지 않는다`() {
        val accumulator = DrivingDistanceAccumulator()

        accumulator.add(37.5665, 126.9780)
        accumulator.add(37.5665, 126.9780)

        assertEquals(0.0, accumulator.totalMeters, 0.0)
    }
}
