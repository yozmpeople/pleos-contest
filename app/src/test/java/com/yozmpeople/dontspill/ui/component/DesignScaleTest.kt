package com.yozmpeople.dontspill.ui.component

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.junit.Assert.assertEquals
import org.junit.Test

class DesignScaleTest {

    private val delta = 0.0001f

    @Test
    fun `S-17 landscape 창은 배율 1이다`() {
        // when
        val scale = designScaleOf(DpSize(1700.dp, 1184.dp), LandscapeDesignSize)

        // then
        assertEquals(1f, scale.value, delta)
    }

    @Test
    fun `가로·세로 비율 중 작은 쪽을 배율로 쓴다`() {
        // given: L-12.9 landscape. 가로 1274/1700, 세로 855/1184
        val available = DpSize(1274.dp, 855.dp)

        // when
        val scale = designScaleOf(available, LandscapeDesignSize)

        // then
        assertEquals(855f / 1184f, scale.value, delta)
    }

    @Test
    fun `시안보다 큰 창에서도 배율은 1을 넘지 않는다`() {
        // when
        val scale = designScaleOf(DpSize(2000.dp, 1500.dp), LandscapeDesignSize)

        // then
        assertEquals(1f, scale.value, delta)
    }

    @Test
    fun `세로 비율은 minHeightRatio 아래로 내리지 않는다`() {
        // given: W-Standard landscape. 세로 810/1184는 0.75보다 작다
        val available = DpSize(1640.dp, 810.dp)

        // when
        val scale = designScaleOf(available, LandscapeDesignSize, minHeightRatio = 0.75f)

        // then
        assertEquals(0.75f, scale.value, delta)
    }

    @Test
    fun `높이가 무한대이면 가로 비율만으로 정한다`() {
        // when
        val scale = designScaleOf(DpSize(840.dp, Dp.Infinity), LandscapeDesignSize)

        // then
        assertEquals(840f / 1700f, scale.value, delta)
    }

    @Test
    fun `글자는 최소 글자 크기 12sp 아래로 줄이지 않는다`() {
        // given
        val style = TextStyle(fontSize = 16.sp, lineHeight = 24.sp)

        // when
        val scaled = DesignScale(0.5f).text(style)

        // then: 16sp × 0.5 = 8sp는 12sp에 걸려 배율 0.75로 멈춘다
        assertEquals(12f, scaled.fontSize.value, delta)
        assertEquals(18f, scaled.lineHeight.value, delta)
    }
}
