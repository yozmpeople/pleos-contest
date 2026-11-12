package com.yozmpeople.dontspill.ui.component

import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.computeWindowSizeClass
import org.junit.Assert.assertEquals
import org.junit.Test

class WindowLayoutTest {

    // currentWindowAdaptiveInfo(supportLargeAndXLargeWidth = true)와 같은 V2 구간으로 계산한다.
    private fun sizeClassOf(widthDp: Int, heightDp: Int): WindowSizeClass =
        WindowSizeClass.BREAKPOINTS_V2.computeWindowSizeClass(widthDp, heightDp)

    @Test
    fun `Pleos 프리셋의 portrait 창은 모두 Portrait다`() {
        // given: Connect-L-12.9, WF, W-Standard, W-Extended, S-14.6, S-17
        val portraitWindows = listOf(628 to 855, 648 to 800, 800 to 810, 800 to 1400, 672 to 948, 840 to 1184)

        portraitWindows.forEach { (width, height) ->
            // when
            val layout = windowLayoutOf(sizeClassOf(width, height))

            // then
            assertEquals("${width}x$height", WindowLayout.Portrait, layout)
        }
    }

    @Test
    fun `Pleos 프리셋의 landscape 창은 모두 Landscape다`() {
        // given: Connect-WF, L-12.9, S-14.6, W-Standard, W-Extended, S-17
        val landscapeWindows = listOf(1256 to 800, 1274 to 855, 1360 to 948, 1640 to 810, 1640 to 1400, 1700 to 1184)

        landscapeWindows.forEach { (width, height) ->
            // when
            val layout = windowLayoutOf(sizeClassOf(width, height))

            // then
            assertEquals("${width}x$height", WindowLayout.Landscape, layout)
        }
    }

    @Test
    fun `창 너비 1199dp는 Portrait다`() {
        // when
        val layout = windowLayoutOf(sizeClassOf(1199, 800))

        // then
        assertEquals(WindowLayout.Portrait, layout)
    }

    @Test
    fun `창 너비 1200dp는 Landscape다`() {
        // when
        val layout = windowLayoutOf(sizeClassOf(1200, 800))

        // then
        assertEquals(WindowLayout.Landscape, layout)
    }

    @Test
    fun `프리셋보다 좁은 창 너비 500dp는 Portrait다`() {
        // when
        val layout = windowLayoutOf(sizeClassOf(500, 800))

        // then
        assertEquals(WindowLayout.Portrait, layout)
    }
}
