package com.yozmpeople.dontspill.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role

// 누르는 동안 줄어드는 비율
private const val PRESSED_SCALE = 0.92f

// 이미지 버튼의 눌림 효과. 기본 물결(ripple)은 이미지의 투명 여백까지 네모나게 그려져서,
// 물결 대신 누르는 동안 살짝 작아졌다가 돌아오게 한다
@Composable
fun Modifier.pressScaleClickable(role: Role = Role.Button, onClick: () -> Unit): Modifier {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) PRESSED_SCALE else 1f, label = "pressScale")
    return this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .clickable(interactionSource = interactionSource, indication = null, role = role, onClick = onClick)
}
