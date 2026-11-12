package com.yozmpeople.dontspill.ui.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yozmpeople.dontspill.R
import com.yozmpeople.dontspill.common.PleosPreviews
import com.yozmpeople.dontspill.ui.component.AppContentFrame
import com.yozmpeople.dontspill.ui.component.LandscapeDesignSize
import com.yozmpeople.dontspill.ui.component.scaled
import com.yozmpeople.dontspill.ui.theme.ChaduckColors
import com.yozmpeople.dontspill.ui.theme.ChaduckTheme
import com.yozmpeople.dontspill.ui.theme.textTitle

private val TitleFontSize = 128.sp
private val TitleLineHeight = 128.sp

// 테두리 두께는 시안 수치가 없어 정한 값이다. 획 가운데 기준으로 그려서 글자 밖으로는 절반만 보인다.
private val TitleStrokeWidth = 22.dp

// 시안에서 제목 윗변은 화면 위(공통 여백 포함)에서 900dp다. 시안 높이(1184dp)에 대한 비율로 바꿔
// 창 높이가 시안과 달라도 같은 높이 비율에 둔다.
private val TitleTopFraction = 900f / LandscapeDesignSize.height.value

// 배경은 AppContentFrame의 background 칸에 SplashBackground로 그린다. 이 화면은 제목만 그린다.
// 제목 위치는 공통 여백을 포함한 높이 기준이라 세로 여백은 쓰지 않고 가로 여백만 쓴다.
@Composable
fun SplashContent(contentPadding: PaddingValues, modifier: Modifier = Modifier) {
    val layoutDirection = LocalLayoutDirection.current
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .padding(
                start = contentPadding.calculateStartPadding(layoutDirection),
                end = contentPadding.calculateEndPadding(layoutDirection)
            )
    ) {
        val titleStyle = MaterialTheme.typography.textTitle
            .copy(fontSize = TitleFontSize, lineHeight = TitleLineHeight, textAlign = TextAlign.Center)
            .scaled()
        val strokeWidthPx = with(LocalDensity.current) { TitleStrokeWidth.scaled.toPx() }
        val titleModifier = Modifier
            .fillMaxWidth()
            .offset(y = maxHeight * TitleTopFraction)
        // 테두리 글자 위에 채운 글자를 겹쳐 그려 글자 안쪽은 채운 색만 보이게 한다.
        Text(
            text = stringResource(R.string.splash_title),
            modifier = titleModifier,
            style = titleStyle.copy(drawStyle = Stroke(width = strokeWidthPx, join = StrokeJoin.Round)),
            color = ChaduckColors.Primary.Normal
        )
        Text(
            text = stringResource(R.string.splash_title),
            modifier = titleModifier,
            style = titleStyle,
            color = ChaduckColors.Background.Alternative
        )
    }
}

// 스플래시 배경은 고른 캐릭터와 상관없이 고정 이미지다.
@Composable
fun SplashBackground(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(R.drawable.img_background_splash),
        contentDescription = null,
        modifier = modifier,
        contentScale = ContentScale.Crop
    )
}

@PleosPreviews
@Composable
private fun SplashContentPreview() {
    ChaduckTheme {
        AppContentFrame(
            background = { SplashBackground(Modifier.matchParentSize()) }
        ) { contentPadding ->
            SplashContent(contentPadding)
        }
    }
}
