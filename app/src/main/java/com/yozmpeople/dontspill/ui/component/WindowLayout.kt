package com.yozmpeople.dontspill.ui.component

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.window.core.layout.WindowSizeClass

enum class WindowLayout { Portrait, Landscape }

// 창 너비가 Large(1200dp) 이상이면 Landscape, 미만이면 Portrait.
fun windowLayoutOf(windowSizeClass: WindowSizeClass): WindowLayout =
    if (windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_LARGE_LOWER_BOUND)) {
        WindowLayout.Landscape
    } else {
        WindowLayout.Portrait
    }

// 기본값으로는 Large 구간을 계산하지 않으므로 supportLargeAndXLargeWidth를 켠다.
@Composable
fun currentWindowLayout(): WindowLayout {
    return windowLayoutOf(currentWindowAdaptiveInfo(supportLargeAndXLargeWidth = true).windowSizeClass)
}
