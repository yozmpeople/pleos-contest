package com.yozmpeople.dontspill.ui.driving

import org.junit.Assert.assertEquals
import org.junit.Test

class DrivingTimeFormatTest {

    @Test
    fun `0초는 00시 00분 00초로 보인다`() {
        assertEquals("00:00:00", formatDrivingTime(0))
    }

    @Test
    fun `60초가 넘으면 분과 초로 나눠 보인다`() {
        // 12분 34초
        assertEquals("00:12:34", formatDrivingTime(754))
    }

    @Test
    fun `1시간이 넘으면 시를 채운다`() {
        // 1시간 1분 1초
        assertEquals("01:01:01", formatDrivingTime(3661))
    }
}
