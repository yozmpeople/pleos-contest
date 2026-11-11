package com.yozmpeople.dontspill.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yozmpeople.dontspill.R
import com.yozmpeople.dontspill.common.PleosPortraitPreviews
import com.yozmpeople.dontspill.ui.character.Character
import com.yozmpeople.dontspill.ui.component.AppContentFrame
import com.yozmpeople.dontspill.ui.component.CharacterBackground
import com.yozmpeople.dontspill.ui.component.ImageButton
import com.yozmpeople.dontspill.ui.component.ImagePanel
import com.yozmpeople.dontspill.ui.component.InfoGuide
import com.yozmpeople.dontspill.ui.component.WindowLayout
import com.yozmpeople.dontspill.ui.component.currentWindowLayout
import com.yozmpeople.dontspill.ui.component.scaled
import com.yozmpeople.dontspill.ui.theme.ChaduckColors
import com.yozmpeople.dontspill.ui.theme.ChaduckTheme
import com.yozmpeople.dontspill.ui.theme.textBold
import com.yozmpeople.dontspill.ui.theme.textTitle

// 가로·세로 시안에서 크기만 다른 요소의 수치
private class HomeDimens(
    val scorePanelSize: DpSize,
    val scoreTextStartPadding: Dp,
    val scoreLabelFontSize: TextUnit,
    val scoreFontSize: TextUnit,
    val topButtonSize: DpSize,
    val namePanelSize: DpSize,
    val nameFontSize: TextUnit,
    val arrowButtonSpacing: Dp,
    val guideIconSize: Dp,
    val guideFontSize: TextUnit
)

// 시안 수치 (사용자, 2026-11-11)
private val LandscapeDimens = HomeDimens(
    scorePanelSize = DpSize(573.dp, 126.dp),
    scoreTextStartPadding = 50.dp,
    scoreLabelFontSize = 36.sp,
    scoreFontSize = 64.sp,
    topButtonSize = DpSize(112.dp, 116.dp),
    namePanelSize = DpSize(514.dp, 125.dp),
    nameFontSize = 52.sp,
    arrowButtonSpacing = 43.dp,
    guideIconSize = 42.dp,
    guideFontSize = 32.sp
)

// 세로 시안 수치 (사용자, 2026-11-11)
private val PortraitDimens = HomeDimens(
    scorePanelSize = DpSize(539.dp, 118.dp),
    scoreTextStartPadding = 40.dp,
    scoreLabelFontSize = 32.sp,
    scoreFontSize = 56.sp,
    topButtonSize = DpSize(102.dp, 105.64286.dp),
    namePanelSize = DpSize(429.dp, 105.dp),
    nameFontSize = 44.sp,
    arrowButtonSpacing = 36.dp,
    guideIconSize = 32.dp,
    guideFontSize = 26.sp
)

// 시안 수치 (사용자, 2026-11-11)
private val SpeechBubbleSize = DpSize(415.dp, 114.dp)
private val SelectedCharacterSize = 500.dp
private val SideCharacterSize = 350.dp
private val SideCharacterAlpha = 0.6f
private val CarouselSpacing = 75.dp
private val ArrowButtonSize = DpSize(81.dp, 80.dp)
private val GuideTopSpacing = 73.dp

private val TopButtonSpacing = 8.dp
private val SpeechBubbleTopSpacing = 80.dp

// 세로 화면은 공통 여백 대신 홈 시안의 여백을 쓴다 (사용자, 2026-11-11)
private val PortraitPaddingTop = 42.dp
private val PortraitPaddingBottom = 70.dp
private val PortraitPaddingHorizontal = 37.dp

// 세로 시안 간격 (사용자, 2026-11-11)
private val PortraitSpeechBubbleTopSpacing = 138.dp
private val PortraitNameRowTopSpacing = 27.dp
private val PortraitGuideTopSpacing = 66.dp

// 말풍선이 가운데 캐릭터 이미지 위쪽과 겹친다. 캐릭터 이미지 안쪽 여백 때문이다 (사용자, 2026-11-11, 시안 눈대중)
private val SpeechBubbleOverlap = 30.dp

// 캐릭터와 이름표 사이 최소 간격 (사용자, 2026-11-11)
private val NameRowMinSpacing = 1.dp

// 점수 패널 오른쪽은 트로피 자리라 글자는 왼쪽부터 놓는다. 위아래는 가운데다(가로 31, 세로 30). (사용자, 2026-11-11)
private val ScoreTextSpacing = 16.dp

// 말풍선 이미지 아래쪽은 꼬리라 글자를 위로 올린다. 위 30, 아래 50 (사용자, 2026-11-11)
// 이미지 높이(114dp)의 가운데에 두면 꼬리 때문에 몸통보다 아래로 처진다 (사용자, 2026-11-12)
// 몸통은 이미지 위 약 5dp~91dp라 몸통 가운데(약 48dp)와 이 여백의 글자 가운데(47dp)가 거의 같다
private val SpeechTextTopPadding = 30.dp
private val SpeechTextBottomPadding = 50.dp

// 시안 수치 (사용자, 2026-11-11)
private val SpeechFontSize = 32.sp
private val SpeechLineHeight = 32.sp

@Composable
fun HomeScreen(
    contentPadding: PaddingValues,
    onTopButtonClick: (HomeTopButton) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    HomeContent(
        uiState = uiState,
        contentPadding = contentPadding,
        onPrevious = viewModel::onPrevious,
        onNext = viewModel::onNext,
        onTopButtonClick = onTopButtonClick
    )
}

// 배경은 AppContentFrame이 그린다. 창 방향에 따라 배치가 달라서 레이아웃을 나눈다.
@Composable
fun HomeContent(
    uiState: HomeUiState,
    contentPadding: PaddingValues,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onTopButtonClick: (HomeTopButton) -> Unit,
    modifier: Modifier = Modifier
) {
    when (currentWindowLayout()) {
        WindowLayout.Landscape -> LandscapeHomeContent(
            uiState = uiState,
            contentPadding = contentPadding,
            onPrevious = onPrevious,
            onNext = onNext,
            onTopButtonClick = onTopButtonClick,
            modifier = modifier
        )

        WindowLayout.Portrait -> PortraitHomeContent(
            uiState = uiState,
            onPrevious = onPrevious,
            onNext = onNext,
            onTopButtonClick = onTopButtonClick,
            modifier = modifier
        )
    }
}

@Composable
private fun LandscapeHomeContent(
    uiState: HomeUiState,
    contentPadding: PaddingValues,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onTopButtonClick: (HomeTopButton) -> Unit,
    modifier: Modifier = Modifier
) {
    val dimens = LandscapeDimens
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(contentPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ScorePanel(uiState.totalScore, dimens)
            Row(horizontalArrangement = Arrangement.spacedBy(TopButtonSpacing.scaled)) {
                TopButtons(dimens, onTopButtonClick)
            }
        }
        Spacer(Modifier.height(SpeechBubbleTopSpacing.scaled))
        // 캐릭터와 겹치는 만큼 차지하는 높이를 줄이고, 캐릭터보다 위에 그린다.
        SpeechBubble(
            text = stringResource(uiState.character.speechRes),
            modifier = Modifier
                .overlapBottom(SpeechBubbleOverlap.scaled)
                .zIndex(1f)
        )
        CharacterCarousel(uiState)
        // 캐릭터 이미지 안쪽에 여백이 있어 시안에서는 이름표가 거의 붙어 있다.
        // 세로가 긴 화면에서 남는 공간은 여기로 받아 캐릭터가 아래로 처지지 않게 한다.
        Spacer(Modifier.height(NameRowMinSpacing.scaled))
        Spacer(Modifier.weight(1f))
        CharacterNameRow(uiState.character, dimens, onPrevious, onNext)
        Spacer(Modifier.height(GuideTopSpacing.scaled))
        InfoGuide(stringResource(R.string.home_guide), dimens.guideIconSize, dimens.guideFontSize)
    }
}

// 세로 화면은 공통 여백(contentPadding)을 쓰지 않고 홈 시안의 여백을 쓴다.
// 상단 버튼은 오른쪽에 세로로 쌓여 말풍선 높이까지 내려오므로 따로 겹쳐 그린다. 양옆 캐릭터는 없다.
@Composable
private fun PortraitHomeContent(
    uiState: HomeUiState,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onTopButtonClick: (HomeTopButton) -> Unit,
    modifier: Modifier = Modifier
) {
    val dimens = PortraitDimens
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(
                start = PortraitPaddingHorizontal.scaled,
                top = PortraitPaddingTop.scaled,
                end = PortraitPaddingHorizontal.scaled,
                bottom = PortraitPaddingBottom.scaled
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ScorePanel(uiState.totalScore, dimens, Modifier.align(Alignment.Start))
            Spacer(Modifier.height(PortraitSpeechBubbleTopSpacing.scaled))
            SpeechBubble(
                text = stringResource(uiState.character.speechRes),
                modifier = Modifier
                    .overlapBottom(SpeechBubbleOverlap.scaled)
                    .zIndex(1f)
            )
            // 세로가 긴 화면에서 남는 높이는 캐릭터를 키우는 데 쓴다. 시안 높이에서는 500dp 그대로다.
            // 너비에 막히면 그 크기에서 멈추고, 남는 높이는 캐릭터 아래에 둔다.
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.TopCenter
            ) {
                SelectedCharacter(
                    character = uiState.character,
                    modifier = Modifier.aspectRatio(1f, matchHeightConstraintsFirst = true)
                )
            }
            Spacer(Modifier.height(PortraitNameRowTopSpacing.scaled))
            CharacterNameRow(uiState.character, dimens, onPrevious, onNext)
            Spacer(Modifier.height(PortraitGuideTopSpacing.scaled))
            InfoGuide(stringResource(R.string.home_guide), dimens.guideIconSize, dimens.guideFontSize)
        }
        Column(
            modifier = Modifier.align(Alignment.TopEnd),
            verticalArrangement = Arrangement.spacedBy(TopButtonSpacing.scaled)
        ) {
            TopButtons(dimens, onTopButtonClick)
        }
    }
}

// 그린 크기는 그대로 두고, 아래쪽 overlap만큼 차지하는 높이를 줄여 다음 요소와 겹치게 한다.
private fun Modifier.overlapBottom(overlap: Dp): Modifier = layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)
    val height = (placeable.height - overlap.roundToPx()).coerceAtLeast(0)
    layout(placeable.width, height) { placeable.place(0, 0) }
}

@Composable
private fun ScorePanel(score: Int, dimens: HomeDimens, modifier: Modifier = Modifier) {
    ImagePanel(R.drawable.img_panel_score, dimens.scorePanelSize, modifier, contentAlignment = Alignment.CenterStart) {
        Row(
            modifier = Modifier.padding(start = dimens.scoreTextStartPadding.scaled),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.home_score_label),
                style = MaterialTheme.typography.textBold.copy(
                    fontSize = dimens.scoreLabelFontSize,
                    lineHeight = dimens.scoreLabelFontSize,
                    textAlign = TextAlign.Center
                ).scaled(),
                color = ChaduckColors.Primary.Normal
            )
            Spacer(Modifier.width(ScoreTextSpacing.scaled))
            Text(
                text = score.toString(),
                style = MaterialTheme.typography.textTitle.copy(
                    fontSize = dimens.scoreFontSize,
                    lineHeight = dimens.scoreFontSize,
                    textAlign = TextAlign.Center
                ).scaled(),
                color = ChaduckColors.Primary.Normal
            )
        }
    }
}

// 상단 버튼을 HomeTopButton 순서대로 그린다. 가로·세로 배치는 감싸는 Row·Column이 정한다.
@Composable
private fun TopButtons(dimens: HomeDimens, onClick: (HomeTopButton) -> Unit) {
    HomeTopButton.entries.forEach { button ->
        Image(
            painter = painterResource(button.imageRes),
            contentDescription = stringResource(button.descriptionRes),
            modifier = Modifier
                .size(dimens.topButtonSize.scaled)
                .clickable(onClick = { onClick(button) })
        )
    }
}

@Composable
private fun SpeechBubble(text: String, modifier: Modifier = Modifier) {
    ImagePanel(R.drawable.img_panel_speechbubble, SpeechBubbleSize, modifier) {
        Text(
            text = text,
            modifier = Modifier.padding(top = SpeechTextTopPadding.scaled, bottom = SpeechTextBottomPadding.scaled),
            style = MaterialTheme.typography.textBold.copy(
                fontSize = SpeechFontSize,
                lineHeight = SpeechLineHeight,
                textAlign = TextAlign.Center
            ).scaled(),
            color = ChaduckColors.Primary.Normal
        )
    }
}

// 고른 캐릭터는 가운데에 크게, 이전·다음 캐릭터는 양옆에 작고 옅게 그린다. 세 캐릭터의 위쪽을 맞춘다.
@Composable
private fun CharacterCarousel(uiState: HomeUiState) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(CarouselSpacing.scaled),
        verticalAlignment = Alignment.Top
    ) {
        SideCharacter(uiState.previousCharacter)
        SelectedCharacter(uiState.character, Modifier.size(SelectedCharacterSize.scaled))
        SideCharacter(uiState.nextCharacter)
    }
}

@Composable
private fun SelectedCharacter(character: Character, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(character.imageRes),
        contentDescription = stringResource(character.displayNameRes),
        modifier = modifier
    )
}

@Composable
private fun SideCharacter(character: Character) {
    Image(
        painter = painterResource(character.imageRes),
        contentDescription = null,
        modifier = Modifier
            .size(SideCharacterSize.scaled)
            .alpha(SideCharacterAlpha)
    )
}

@Composable
private fun CharacterNameRow(character: Character, dimens: HomeDimens, onPrevious: () -> Unit, onNext: () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(dimens.arrowButtonSpacing.scaled),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ImageButton(
            R.drawable.img_button_previous,
            stringResource(R.string.home_previous_character),
            ArrowButtonSize,
            onPrevious
        )
        ImagePanel(R.drawable.img_panel_title, dimens.namePanelSize) {
            Text(
                text = stringResource(character.displayNameRes),
                style = MaterialTheme.typography.textTitle.copy(
                    fontSize = dimens.nameFontSize,
                    lineHeight = dimens.nameFontSize,
                    textAlign = TextAlign.Center
                ).scaled(),
                color = ChaduckColors.Primary.Normal
            )
        }
        ImageButton(R.drawable.img_button_next, stringResource(R.string.home_next_character), ArrowButtonSize, onNext)
    }
}

@Composable
private fun HomePreviewFrame(character: Character) {
    ChaduckTheme {
        AppContentFrame(
            background = { CharacterBackground(character, Modifier.matchParentSize()) }
        ) { contentPadding ->
            HomeContent(
                uiState = HomeUiState.of(character),
                contentPadding = contentPadding,
                onPrevious = {},
                onNext = {},
                onTopButtonClick = {}
            )
        }
    }
}

@PleosPortraitPreviews
@Composable
private fun HomeContentPreview() {
    HomePreviewFrame(Character.entries.first())
}
