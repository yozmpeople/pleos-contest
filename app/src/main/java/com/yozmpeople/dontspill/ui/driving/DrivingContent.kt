package com.yozmpeople.dontspill.ui.driving

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yozmpeople.dontspill.R
import com.yozmpeople.dontspill.common.PleosLandscapePreviews
import com.yozmpeople.dontspill.common.PleosPortraitPreviews
import com.yozmpeople.dontspill.ui.character.Character
import com.yozmpeople.dontspill.ui.component.AppContentFrame
import com.yozmpeople.dontspill.ui.component.CharacterBackground
import com.yozmpeople.dontspill.ui.component.InfoGuide
import com.yozmpeople.dontspill.ui.component.PillButton
import com.yozmpeople.dontspill.ui.component.TitlePanel
import com.yozmpeople.dontspill.ui.component.WindowLayout
import com.yozmpeople.dontspill.ui.component.currentWindowLayout
import com.yozmpeople.dontspill.ui.component.scaled
import com.yozmpeople.dontspill.ui.theme.ChaduckTheme

// 가로·세로 시안에서 크기만 다른 요소의 수치
private class DrivingDimens(
    val timePanelSize: DpSize,
    val timeFontSize: TextUnit,
    val characterTopSpacing: Dp,
    val endButtonTopSpacing: Dp,
    val endButtonSize: DpSize,
    val endButtonFontSize: TextUnit,
    val guideTopSpacing: Dp,
    val guideIconSize: Dp,
    val guideFontSize: TextUnit
)

private val LandscapeDimens = DrivingDimens(
    timePanelSize = DpSize(514.dp, 125.dp),
    timeFontSize = 52.sp,
    characterTopSpacing = 39.dp,
    endButtonTopSpacing = 41.dp,
    endButtonSize = DpSize(512.dp, 170.dp),
    endButtonFontSize = 48.sp,
    guideTopSpacing = 55.dp,
    guideIconSize = 42.dp,
    guideFontSize = 32.sp
)

private val PortraitDimens = DrivingDimens(
    timePanelSize = DpSize(421.dp, 104.dp),
    timeFontSize = 44.sp,
    characterTopSpacing = 81.dp,
    endButtonTopSpacing = 46.dp,
    endButtonSize = DpSize(400.dp, 132.dp),
    endButtonFontSize = 40.sp,
    guideTopSpacing = 64.dp,
    guideIconSize = 32.dp,
    guideFontSize = 26.sp
)

// 세로 화면은 공통 여백 대신 주행 중 시안의 여백을 쓴다. 모두 가운데 정렬이라 좌우 여백은 없다
private val PortraitPaddingTop = 117.dp
private val PortraitPaddingBottom = 66.dp

// 배경은 AppContentFrame이 그린다. 가로·세로 모두 요소를 가로 가운데에 세로로 쌓고, 수치와 여백만 다르다.
// 캐릭터는 여백 안에서 다른 요소를 놓고 남는 높이를 다 채운다. 너비에 막히면 그 크기에서 멈춘다.
@Composable
fun DrivingContent(
    uiState: DrivingUiState,
    contentPadding: PaddingValues,
    onEndDriving: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (dimens, padding) = when (currentWindowLayout()) {
        WindowLayout.Landscape -> LandscapeDimens to contentPadding

        WindowLayout.Portrait -> PortraitDimens to PaddingValues(
            top = PortraitPaddingTop.scaled,
            bottom = PortraitPaddingBottom.scaled
        )
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(padding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TitlePanel(formatDrivingTime(uiState.elapsedSeconds), dimens.timePanelSize, dimens.timeFontSize)
        Spacer(Modifier.height(dimens.characterTopSpacing.scaled))
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(uiState.character.drivingImageRes),
                contentDescription = stringResource(uiState.character.displayNameRes),
                modifier = Modifier.aspectRatio(1f, matchHeightConstraintsFirst = true)
            )
        }
        Spacer(Modifier.height(dimens.endButtonTopSpacing.scaled))
        PillButton(stringResource(R.string.driving_end), dimens.endButtonSize, dimens.endButtonFontSize, onEndDriving)
        Spacer(Modifier.height(dimens.guideTopSpacing.scaled))
        InfoGuide(stringResource(R.string.driving_guide), dimens.guideIconSize, dimens.guideFontSize)
    }
}

@Composable
private fun DrivingPreviewFrame() {
    val character = Character.WaterCup
    ChaduckTheme {
        AppContentFrame(
            background = { CharacterBackground(character, Modifier.matchParentSize()) }
        ) { contentPadding ->
            DrivingContent(
                uiState = DrivingUiState(character = character, elapsedSeconds = 754),
                contentPadding = contentPadding,
                onEndDriving = {}
            )
        }
    }
}

// @PleosPortraitPreviews
@PleosLandscapePreviews
@Composable
private fun DrivingContentPreview() {
    DrivingPreviewFrame()
}
