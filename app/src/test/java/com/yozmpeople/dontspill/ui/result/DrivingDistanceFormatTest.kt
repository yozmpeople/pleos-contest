package com.yozmpeople.dontspill.ui.result

import org.junit.Assert.assertEquals
import org.junit.Test

class DrivingDistanceFormatTest {

    @Test
    fun `미터를 km로 바꿔 소수 첫째 자리까지 보인다`() {
        assertEquals("1.7km", formatDrivingDistance(1_700))
    }

    @Test
    fun `0미터는 0점0km로 보인다`() {
        assertEquals("0.0km", formatDrivingDistance(0))
    }

    @Test
    fun `둘째 자리는 반올림한다`() {
        assertEquals("1.8km", formatDrivingDistance(1_750))
    }
}
