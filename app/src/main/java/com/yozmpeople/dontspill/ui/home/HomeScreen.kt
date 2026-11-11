package com.yozmpeople.dontspill.ui.home

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yozmpeople.dontspill.R
import com.yozmpeople.dontspill.common.PleosLandscapePreviews
import com.yozmpeople.dontspill.common.PleosPreviews
import com.yozmpeople.dontspill.ui.character.Character
import com.yozmpeople.dontspill.ui.component.AppContentFrame
import com.yozmpeople.dontspill.ui.component.CharacterBackground
import com.yozmpeople.dontspill.ui.component.scaled
import com.yozmpeople.dontspill.ui.theme.ChaduckColors
import com.yozmpeople.dontspill.ui.theme.ChaduckTheme
import com.yozmpeople.dontspill.ui.theme.textBold
import com.yozmpeople.dontspill.ui.theme.textSemiBold
import com.yozmpeople.dontspill.ui.theme.textTitle

// 시안 수치 (사용자, 2026-11-11)
private val ScorePanelSize = DpSize(573.dp, 126.dp)
private val TopButtonSize = DpSize(112.dp, 116.dp)
private val SpeechBubbleSize = DpSize(415.dp, 114.dp)
private val SelectedCharacterSize = 500.dp
private val SideCharacterSize = 350.dp
private val SideCharacterAlpha = 0.6f
private val CarouselSpacing = 75.dp
private val NamePanelSize = DpSize(514.dp, 125.dp)
private val ArrowButtonSize = DpSize(81.dp, 80.dp)
private val ArrowButtonSpacing = 43.dp
private val GuideTopSpacing = 73.dp
private val GuideIconSize = 42.dp
private val GuideIconPadding = 1.dp
private val GuideIconSpacing = 8.dp

private val TopButtonSpacing = 8.dp
private val SpeechBubbleTopSpacing = 80.dp

// 말풍선이 가운데 캐릭터 이미지 위쪽과 겹친다. 캐릭터 이미지 안쪽 여백 때문이다 (사용자, 2026-11-11, 시안 눈대중)
private val SpeechBubbleOverlap = 30.dp

// 캐릭터와 이름표 사이 최소 간격 (사용자, 2026-11-11)
private val NameRowMinSpacing = 1.dp

// 점수 패널 오른쪽은 트로피 자리라 글자는 왼쪽부터 놓는다. 위아래는 가운데다(점수 글자 위아래 31). (사용자, 2026-11-11)
private val ScoreTextStartPadding = 50.dp
private val ScoreTextSpacing = 16.dp

// 말풍선 이미지 아래쪽은 꼬리라 글자를 위로 올린다. 위 30, 아래 50 (사용자, 2026-11-11)
private val SpeechTextTopPadding = 30.dp
private val SpeechTextBottomPadding = 50.dp

// 시안 수치 (사용자, 2026-11-11)
private val NameFontSize = 52.sp
private val NameLineHeight = 52.sp
private val ScoreLabelFontSize = 36.sp
private val ScoreLabelLineHeight = 36.sp
private val ScoreFontSize = 64.sp
private val ScoreLineHeight = 64.sp
private val SpeechFontSize = 32.sp
private val SpeechLineHeight = 32.sp
private val GuideFontSize = 32.sp
private val GuideLineHeight = 32.sp

@Composable
fun HomeScreen(
    contentPadding: PaddingValues,
    onAchievementClick: () -> Unit,
    onHelpClick: () -> Unit,
    onLeaderboardClick: () -> Unit,
    onHistoryClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    HomeContent(
        uiState = uiState,
        contentPadding = contentPadding,
        onPrevious = viewModel::onPrevious,
        onNext = viewModel::onNext,
        onAchievementClick = onAchievementClick,
        onHelpClick = onHelpClick,
        onLeaderboardClick = onLeaderboardClick,
        onHistoryClick = onHistoryClick
    )
}

// 배경은 AppContentFrame이 그린다.
@Composable
fun HomeContent(
    uiState: HomeUiState,
    contentPadding: PaddingValues,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onAchievementClick: () -> Unit,
    onHelpClick: () -> Unit,
    onLeaderboardClick: () -> Unit,
    onHistoryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
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
            ScorePanel(uiState.totalScore)
            Row(horizontalArrangement = Arrangement.spacedBy(TopButtonSpacing.scaled)) {
                TopButton(R.drawable.img_button_achievement, R.string.home_achievement, onAchievementClick)
                TopButton(R.drawable.img_button_help, R.string.home_help, onHelpClick)
                TopButton(R.drawable.img_button_leaderboard, R.string.home_leaderboard, onLeaderboardClick)
                TopButton(R.drawable.img_button_history, R.string.home_history, onHistoryClick)
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
        CharacterNameRow(uiState.character, onPrevious, onNext)
        Spacer(Modifier.height(GuideTopSpacing.scaled))
        Guide()
    }
}

// 그린 크기는 그대로 두고, 아래쪽 overlap만큼 차지하는 높이를 줄여 다음 요소와 겹치게 한다.
private fun Modifier.overlapBottom(overlap: Dp): Modifier = layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)
    val height = (placeable.height - overlap.roundToPx()).coerceAtLeast(0)
    layout(placeable.width, height) { placeable.place(0, 0) }
}

// 이미지 위에 글자를 얹는 패널. 이미지는 패널 크기에 맞춰 늘린다.
@Composable
private fun ImagePanel(
    @DrawableRes imageRes: Int,
    size: DpSize,
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.Center,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier.size(size.scaled), contentAlignment = contentAlignment) {
        Image(
            painter = painterResource(imageRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
        content()
    }
}

@Composable
private fun ScorePanel(score: Int) {
    ImagePanel(R.drawable.img_panel_score, ScorePanelSize, contentAlignment = Alignment.CenterStart) {
        Row(
            modifier = Modifier.padding(start = ScoreTextStartPadding.scaled),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.home_score_label),
                style = MaterialTheme.typography.textBold.copy(
                    fontSize = ScoreLabelFontSize,
                    lineHeight = ScoreLabelLineHeight,
                    textAlign = TextAlign.Center
                ).scaled(),
                color = ChaduckColors.Primary.Normal
            )
            Spacer(Modifier.width(ScoreTextSpacing.scaled))
            Text(
                text = score.toString(),
                style = MaterialTheme.typography.textTitle.copy(
                    fontSize = ScoreFontSize,
                    lineHeight = ScoreLineHeight,
                    textAlign = TextAlign.Center
                ).scaled(),
                color = ChaduckColors.Primary.Normal
            )
        }
    }
}

@Composable
private fun TopButton(@DrawableRes imageRes: Int, @StringRes descriptionRes: Int, onClick: () -> Unit) {
    Image(
        painter = painterResource(imageRes),
        contentDescription = stringResource(descriptionRes),
        modifier = Modifier
            .size(TopButtonSize.scaled)
            .clickable(onClick = onClick)
    )
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
        Image(
            painter = painterResource(uiState.character.imageRes),
            contentDescription = stringResource(uiState.character.displayNameRes),
            modifier = Modifier.size(SelectedCharacterSize.scaled)
        )
        SideCharacter(uiState.nextCharacter)
    }
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
private fun CharacterNameRow(character: Character, onPrevious: () -> Unit, onNext: () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(ArrowButtonSpacing.scaled),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ArrowButton(R.drawable.img_button_previous, R.string.home_previous_character, onPrevious)
        ImagePanel(R.drawable.img_panel_title, NamePanelSize) {
            Text(
                text = stringResource(character.displayNameRes),
                style = MaterialTheme.typography.textTitle.copy(
                    fontSize = NameFontSize,
                    lineHeight = NameLineHeight,
                    textAlign = TextAlign.Center
                ).scaled(),
                color = ChaduckColors.Primary.Normal
            )
        }
        ArrowButton(R.drawable.img_button_next, R.string.home_next_character, onNext)
    }
}

// 배율로 작아져도 터치 영역은 최소 크기(48dp) 아래로 줄이지 않는다.
@Composable
private fun ArrowButton(@DrawableRes imageRes: Int, @StringRes descriptionRes: Int, onClick: () -> Unit) {
    Image(
        painter = painterResource(imageRes),
        contentDescription = stringResource(descriptionRes),
        modifier = Modifier
            .minimumInteractiveComponentSize()
            .clickable(onClick = onClick)
            .size(ArrowButtonSize.scaled)
    )
}

@Composable
private fun Guide() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(R.drawable.ic_circle_info),
            contentDescription = null,
            modifier = Modifier
                .padding(GuideIconPadding.scaled)
                .size(GuideIconSize.scaled)
        )
        Spacer(Modifier.width(GuideIconSpacing.scaled))
        Text(
            text = stringResource(R.string.home_guide),
            style = MaterialTheme.typography.textSemiBold.copy(
                fontSize = GuideFontSize,
                lineHeight = GuideLineHeight,
                textAlign = TextAlign.Center
            ).scaled(),
            color = ChaduckColors.Label.Normal
        )
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
                onAchievementClick = {},
                onHelpClick = {},
                onLeaderboardClick = {},
                onHistoryClick = {}
            )
        }
    }
}

@PleosLandscapePreviews
@Composable
private fun HomeContentPreview() {
    HomePreviewFrame(Character.entries.first())
}
