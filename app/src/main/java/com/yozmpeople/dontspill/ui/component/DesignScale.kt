package com.yozmpeople.dontspill.ui.component

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.max
import kotlin.math.min

// 시안 기준 크기. S-17 프리셋의 창 전체 크기다. 화면 여백에도 배율을 곱하므로 여백을 빼지 않는다.
val LandscapeDesignSize = DpSize(1700.dp, 1184.dp)
val PortraitDesignSize = DpSize(840.dp, 1184.dp)

// 지금 창 방향에 맞는 시안 크기
@Composable
fun currentDesignSize(): DpSize = when (currentWindowLayout()) {
    WindowLayout.Landscape -> LandscapeDesignSize
    WindowLayout.Portrait -> PortraitDesignSize
}

// Pleos 최소 글자 크기
private val MinFontSize = 12.sp

// 시안 크기 대비 실제 공간의 배율. 1보다 크게 키우지 않는다.
@JvmInline
value class DesignScale(val value: Float) {

    // 여백·간격·컴포넌트 크기
    fun dp(design: Dp): Dp = design * value

    fun size(design: DpSize): DpSize = design * value

    // 글자 크기·줄 높이·자간에 같은 배율을 곱해 시안 비율을 지킨다.
    // 글자 크기가 최소 글자 크기에 걸리면 줄 높이와 자간도 같은 배율에서 멈춘다.
    // em 단위는 글자 크기를 따라가므로 그대로 둔다.
    fun text(style: TextStyle): TextStyle {
        if (!style.fontSize.isSp) return style
        val textScale = max(value, MinFontSize.value / style.fontSize.value)
        return style.copy(
            fontSize = style.fontSize * textScale,
            lineHeight = style.lineHeight.scaledIfSp(textScale),
            letterSpacing = style.letterSpacing.scaledIfSp(textScale)
        )
    }

    companion object {
        val None = DesignScale(1f)
    }
}

private fun TextUnit.scaledIfSp(scale: Float): TextUnit = if (isSp) this * scale else this

// 가로는 넘치면 안 되므로 가로 비율이 그대로 상한이다. 세로 비율은 minHeightRatio 아래로 내리지 않는다.
// 높이가 무한대(스크롤 안)이면 가로 비율만으로 정해진다.
fun designScaleOf(available: DpSize, design: DpSize, minHeightRatio: Float = 0f): DesignScale {
    val widthRatio = available.width / design.width
    val heightRatio = max(available.height / design.height, minHeightRatio)
    return DesignScale(min(widthRatio, heightRatio).coerceAtMost(1f))
}

// DesignScaleBox 안쪽에서 쓰는 배율. 바깥에서는 1이라 시안 값 그대로다.
val LocalDesignScale = staticCompositionLocalOf { DesignScale.None }

// 이 Box가 받은 공간과 시안 크기로 배율을 정해 안쪽에 내려 준다.
// 화면 전체는 AppContentFrame이 감싼다. 패널처럼 따로 줄이는 영역은 그 영역의 시안 크기로 다시 감싼다.
// 안쪽에서 다시 감싸면 가장 가까운 Box의 배율을 쓴다.
@Composable
fun DesignScaleBox(
    designSize: DpSize,
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    minHeightRatio: Float = 0f,
    content: @Composable BoxWithConstraintsScope.() -> Unit
) {
    BoxWithConstraints(modifier = modifier, contentAlignment = contentAlignment) {
        val scale = designScaleOf(DpSize(maxWidth, maxHeight), designSize, minHeightRatio)
        CompositionLocalProvider(LocalDesignScale provides scale) {
            content()
        }
    }
}

// 시안 값에 지금 영역의 배율을 곱한다. 예: Modifier.padding(48.dp.scaled), style.copy(fontSize = 32.sp).scaled()
val Dp.scaled: Dp
    @Composable @ReadOnlyComposable
    get() = LocalDesignScale.current.dp(this)

val DpSize.scaled: DpSize
    @Composable @ReadOnlyComposable
    get() = LocalDesignScale.current.size(this)

@Composable
@ReadOnlyComposable
fun TextStyle.scaled(): TextStyle = LocalDesignScale.current.text(this)
