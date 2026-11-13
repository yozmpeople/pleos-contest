package com.yozmpeople.dontspill.ui.result

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yozmpeople.dontspill.R
import com.yozmpeople.dontspill.common.PleosLandscapePreviews
import com.yozmpeople.dontspill.common.PleosPortraitPreviews
import com.yozmpeople.dontspill.common.PleosPreviews
import com.yozmpeople.dontspill.ui.character.Character
import com.yozmpeople.dontspill.ui.component.AppContentFrame
import com.yozmpeople.dontspill.ui.component.CharacterBackground
import com.yozmpeople.dontspill.ui.component.PillButton
import com.yozmpeople.dontspill.ui.component.WindowLayout
import com.yozmpeople.dontspill.ui.component.currentWindowLayout
import com.yozmpeople.dontspill.ui.component.scaled
import com.yozmpeople.dontspill.ui.driving.formatDrivingTime
import com.yozmpeople.dontspill.ui.theme.ChaduckColors
import com.yozmpeople.dontspill.ui.theme.ChaduckTheme
import com.yozmpeople.dontspill.ui.theme.textBold
import com.yozmpeople.dontspill.ui.theme.textTitle

// 가로·세로 시안에서 크기만 다른 요소의 수치
private class ResultDimens(
    val stickerHeight: Dp,
    val homeButtonSize: DpSize,
    val homeButtonFontSize: TextUnit,
    val finalScoreFontSize: TextUnit,
    val scoreTopSpacing: Dp,
    val scoreFontSize: TextUnit,
    val infoTopSpacing: Dp,
    val infoFontSize: TextUnit,
    val infoLabelEndSpacing: Dp,
    val infoRowSpacing: Dp
)

// 등급 로고 높이는 이미지 크기(501px ÷ 3 = 167dp), 버튼은 주행 중 가로 버튼 수치다.
// 나머지는 시안 수치가 없어 정한 값이다
private val LandscapeDimens = ResultDimens(
    stickerHeight = 167.dp,
    homeButtonSize = DpSize(512.dp, 170.dp),
    homeButtonFontSize = 48.sp,
    finalScoreFontSize = 54.sp,
    scoreTopSpacing = 20.dp,
    scoreFontSize = 116.sp,
    infoTopSpacing = 80.dp,
    infoFontSize = 44.sp,
    infoLabelEndSpacing = 80.dp,
    infoRowSpacing = 30.dp
)

// 버튼은 주행 중 세로 버튼 수치다. 나머지는 시안 수치가 없어 정한 값이다
private val PortraitDimens = ResultDimens(
    stickerHeight = 167.dp,
    homeButtonSize = DpSize(400.dp, 132.dp),
    homeButtonFontSize = 40.sp,
    finalScoreFontSize = 44.sp,
    scoreTopSpacing = 32.dp,
    scoreFontSize = 104.sp,
    infoTopSpacing = 76.dp,
    infoFontSize = 44.sp,
    infoLabelEndSpacing = 80.dp,
    infoRowSpacing = 20.dp
)

// 가로 결과 패널의 왼쪽 모서리. 오른쪽은 화면 끝에 붙어 있어 앱 창 모서리가 둥글게 잘라 준다
private val LandscapePanelCornerRadius = 64.dp

// 등급별 캐릭터. 가로·세로 같은 크기이고, 공간이 모자라면 남는 높이·너비에 맞춰 줄인다
private val CharacterSize = 560.dp

private val PortraitStickerTopPadding = 50.dp
private val PortraitPanelPaddingTop = 60.dp
private val PortraitPanelPaddingBottom = 80.dp
private val PortraitHomeButtonTopSpacing = 64.dp

// 배경은 AppContentFrame이 그린다. 이번 버전은 최종 점수·주행 시간·주행 거리만 보인다
// 가로는 좌우로 나누고 스크롤하지 않는다. 캐릭터 영역은 공통 여백 중 위·아래만 쓰고 좌우 여백은 쓰지 않는다.
// 결과 패널(흰 배경)은 공통 여백을 무시하고 화면 위·오른쪽·아래 끝까지 채운다
// 캐릭터 영역과 결과 패널은 화면 너비를 반씩 나눈다
// 세로는 공통 여백 없이 위아래로 나누고, 내용이 화면보다 길면 화면 전체를 세로로 스크롤한다.
@Composable
fun ResultContent(
    uiState: ResultUiState,
    contentPadding: PaddingValues,
    onHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (currentWindowLayout()) {
        WindowLayout.Landscape -> LandscapeResult(uiState, contentPadding, onHome, modifier)
        WindowLayout.Portrait -> PortraitResult(uiState, onHome, modifier)
    }
}

// 왼쪽은 등급 로고·캐릭터·홈 버튼을 한 묶음으로 가운데에 놓는다
// 높이가 모자라면 캐릭터만 줄어든다.
@Composable
private fun LandscapeResult(
    uiState: ResultUiState,
    contentPadding: PaddingValues,
    onHome: () -> Unit,
    modifier: Modifier
) {
    val dimens = LandscapeDimens
    Row(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(top = contentPadding.calculateTopPadding(), bottom = contentPadding.calculateBottomPadding()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GradeSticker(uiState.grade, dimens)
            GradeCharacter(uiState, Modifier.weight(1f, fill = false))
            PillButton(
                text = stringResource(R.string.result_home),
                size = dimens.homeButtonSize,
                fontSize = dimens.homeButtonFontSize,
                onClick = onHome
            )
        }
        val cornerRadius = LandscapePanelCornerRadius.scaled
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(
                    color = ChaduckColors.Background.Normal,
                    shape = RoundedCornerShape(topStart = cornerRadius, bottomStart = cornerRadius)
                ),
            contentAlignment = Alignment.Center
        ) {
            ResultInfo(uiState, dimens)
        }
    }
}

@Composable
private fun PortraitResult(uiState: ResultUiState, onHome: () -> Unit, modifier: Modifier) {
    val dimens = PortraitDimens
    // 캐릭터 영역은 등급 로고·캐릭터 높이만큼이고, 결과 패널은 적어도 나머지 높이를 채운다
    // 스크롤 안에서는 높이 제한이 없어서, 바깥에서 화면 크기를 재 두고 패널 최소 높이를 계산한다.
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val characterHeight = PortraitStickerTopPadding.scaled + dimens.stickerHeight.scaled +
            minOf(CharacterSize.scaled, maxWidth)
        val panelMinHeight = (maxHeight - characterHeight).coerceAtLeast(0.dp)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            CharacterArea(
                uiState = uiState,
                dimens = dimens,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = PortraitStickerTopPadding.scaled)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = panelMinHeight)
                    .background(ChaduckColors.Background.Normal)
                    .padding(top = PortraitPanelPaddingTop.scaled, bottom = PortraitPanelPaddingBottom.scaled),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ResultInfo(uiState, dimens)
                Spacer(Modifier.height(PortraitHomeButtonTopSpacing.scaled))
                PillButton(
                    text = stringResource(R.string.result_home),
                    size = dimens.homeButtonSize,
                    fontSize = dimens.homeButtonFontSize,
                    onClick = onHome
                )
            }
        }
    }
}

// 세로 캐릭터 영역. 위에 등급 로고, 그 아래 캐릭터를 놓는다. 캐릭터는 화면 너비에 막히면 줄어든다.
@Composable
private fun CharacterArea(uiState: ResultUiState, dimens: ResultDimens, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        GradeSticker(uiState.grade, dimens)
        GradeCharacter(uiState)
    }
}

// 등급 로고. 높이만 정하고 너비는 이미지 비율을 따른다.
@Composable
private fun GradeSticker(grade: ResultGrade, dimens: ResultDimens) {
    val sticker = painterResource(grade.stickerRes)
    Image(
        painter = sticker,
        contentDescription = stringResource(grade.descriptionRes),
        modifier = Modifier
            .height(dimens.stickerHeight.scaled)
            .aspectRatio(sticker.intrinsicSize.width / sticker.intrinsicSize.height)
    )
}

// 등급별 캐릭터. 시안 크기를 넘지 않고, 높이나 너비에 막히면 그 크기에서 멈춘다.
@Composable
private fun GradeCharacter(uiState: ResultUiState, modifier: Modifier = Modifier) {
    val characterSize = CharacterSize.scaled
    Image(
        painter = painterResource(uiState.grade.characterImageRes(uiState.character)),
        contentDescription = stringResource(uiState.character.displayNameRes),
        modifier = modifier
            .sizeIn(maxWidth = characterSize, maxHeight = characterSize)
            .aspectRatio(1f, matchHeightConstraintsFirst = true)
    )
}

// 최종 점수 → 점수 → 주행 시간·주행 거리. 정보 2줄은 묶음째 가운데에 놓고, 항목명 폭이 같아 값이 나란히 선다.
@Composable
private fun ResultInfo(uiState: ResultUiState, dimens: ResultDimens) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = stringResource(R.string.result_final_score),
            style = MaterialTheme.typography.textBold.copy(
                fontSize = dimens.finalScoreFontSize,
                lineHeight = dimens.finalScoreFontSize
            ).scaled(),
            color = ChaduckColors.Primary.Normal
        )
        Spacer(Modifier.height(dimens.scoreTopSpacing.scaled))
        Text(
            text = stringResource(R.string.result_score, uiState.score),
            style = MaterialTheme.typography.textTitle.copy(
                fontSize = dimens.scoreFontSize,
                lineHeight = dimens.scoreFontSize
            ).scaled(),
            color = ChaduckColors.Label.Normal
        )
        Spacer(Modifier.height(dimens.infoTopSpacing.scaled))
        Column {
            InfoRow(stringResource(R.string.result_driving_time), formatDrivingTime(uiState.drivingSeconds), dimens)
            Spacer(Modifier.height(dimens.infoRowSpacing.scaled))
            InfoRow(
                stringResource(R.string.result_driving_distance),
                formatDrivingDistance(uiState.distanceMeters),
                dimens
            )
        }
    }
}

// 항목명은 700, 값은 800이다
@Composable
private fun InfoRow(label: String, value: String, dimens: ResultDimens) {
    val labelStyle = infoStyle(MaterialTheme.typography.textBold, dimens)
    val valueStyle = infoStyle(MaterialTheme.typography.textTitle, dimens)
    Row {
        Text(text = label, style = labelStyle, color = ChaduckColors.Primary.Normal)
        Spacer(Modifier.width(dimens.infoLabelEndSpacing.scaled))
        Text(text = value, style = valueStyle, color = ChaduckColors.Label.Normal)
    }
}

@Composable
private fun infoStyle(base: TextStyle, dimens: ResultDimens): TextStyle =
    base.copy(fontSize = dimens.infoFontSize, lineHeight = dimens.infoFontSize).scaled()

// 시안 예시 점수: 99점 PERFECT, 75점 GOOD, 68점 BAD
@Composable
private fun ResultPreviewFrame(score: Int, drivingSeconds: Long = 0) {
    val character = Character.WaterCup
    ChaduckTheme {
        AppContentFrame(
            background = { CharacterBackground(character, Modifier.matchParentSize()) }
        ) { contentPadding ->
            ResultContent(
                uiState = ResultUiState(
                    character = character,
                    score = score,
                    drivingSeconds = drivingSeconds,
                    distanceMeters = 1_700
                ),
                contentPadding = contentPadding,
                onHome = {}
            )
        }
    }
}

@PleosLandscapePreviews
//@PleosPortraitPreviews
@Composable
private fun ResultPerfectPreview() {
    ResultPreviewFrame(score = 96, drivingSeconds = 754)
}

//@PleosLandscapePreviews
////@PleosPortraitPreviews
//@Composable
//private fun ResultGoodPreview() {
//    ResultPreviewFrame(score = 75)
//}

//@PleosLandscapePreviews
//@PleosPortraitPreviews
//@Composable
//private fun ResultBadPreview() {
//    ResultPreviewFrame(score = 68)
//}
