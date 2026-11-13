package com.yozmpeople.dontspill.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.yozmpeople.dontspill.common.PleosPreviews
import com.yozmpeople.dontspill.ui.theme.ChaduckTheme

// 모든 화면에 공통인 바깥 여백. 시안(S-17) 값이고 배율을 곱해 쓴다.
private val ScreenPaddingHorizontal = 90.dp
private val ScreenPaddingVertical = 80.dp

// 모든 화면을 감싸는 틀. 시스템 바·컷아웃 인셋 안쪽에서 배율을 정해 내려 준다.
// 배경은 여백을 받지 않고 틀 전체를 채운다. 배경 Composable에는 Modifier.matchParentSize()를 건다.
// 여백은 직접 적용하지 않고 content에 넘긴다. 보통은 Modifier.padding(contentPadding)으로 그대로 쓰고,
// 리더보드처럼 일부 여백을 무시하는 컴포넌트는 필요한 방향 값만 쓴다.
@Composable
fun AppContentFrame(
    modifier: Modifier = Modifier,
    background: @Composable BoxScope.() -> Unit = {},
    content: @Composable BoxScope.(contentPadding: PaddingValues) -> Unit
) {
    DesignScaleBox(
        designSize = currentDesignSize(),
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        background()
        val contentPadding = PaddingValues(
            horizontal = ScreenPaddingHorizontal.scaled,
            vertical = ScreenPaddingVertical.scaled
        )
        content(contentPadding)
    }
}

// 프리셋별 배율과 여백 확인용. 배경은 회색, 여백 안쪽 콘텐츠 영역은 흰색으로 칠한다.
@PleosPreviews
@Composable
private fun AppContentFramePreview() {
    ChaduckTheme {
        AppContentFrame(
            background = { Box(Modifier.matchParentSize().background(Color.LightGray)) }
        ) { contentPadding ->
            Box(
                modifier = Modifier
                    .padding(contentPadding)
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                Text(
                    text = "scale ${LocalDesignScale.current.value}",
                    style = MaterialTheme.typography.bodyLarge.scaled()
                )
            }
        }
    }
}
