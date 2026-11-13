package com.yozmpeople.dontspill.ui.leaderboard

import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
import com.yozmpeople.dontspill.ui.component.BackButton
import com.yozmpeople.dontspill.ui.component.CharacterBackground
import com.yozmpeople.dontspill.ui.component.TitlePanel
import com.yozmpeople.dontspill.ui.component.WindowLayout
import com.yozmpeople.dontspill.ui.component.currentWindowLayout
import com.yozmpeople.dontspill.ui.component.scaled
import com.yozmpeople.dontspill.ui.theme.ChaduckColors
import com.yozmpeople.dontspill.ui.theme.ChaduckTheme
import com.yozmpeople.dontspill.ui.theme.textBold
import com.yozmpeople.dontspill.ui.theme.textTitle

// 가로·세로 시안에서 크기만 다른 요소의 수치
private class LeaderboardDimens(
    val backButtonSize: DpSize,
    val titlePanelSize: DpSize,
    val titleFontSize: TextUnit,
    val listTopSpacing: Dp,
    @DrawableRes val rowImageRes: Int,
    val rowHeight: Dp,
    val rowSpacing: Dp,
    val rowPaddingStart: Dp,
    val rowPaddingEnd: Dp,
    val badgeSize: Dp,
    val badgeFontSize: TextUnit,
    val badgeSmallFontSize: TextUnit,
    val nicknameStartSpacing: Dp,
    val nicknameFontSize: TextUnit,
    // true면 점수를 닉네임 아래 줄에, false면 같은 줄 오른쪽 끝에 놓는다.
    val scoreBelowNickname: Boolean,
    val scoreTopSpacing: Dp,
    val scoreFontSize: TextUnit,
    val myRankingHeight: Dp
)

// 뒤로 가기·제목은 도움말 가로 수치, 행 높이는 행 이미지 크기(픽셀 ÷ 3 = dp)다.
// 나머지는 시안 수치가 없어 정한 값이다. 1~3위가 다 보이고 4위 위쪽만 보이게 맞췄다
private val LandscapeDimens = LeaderboardDimens(
    backButtonSize = DpSize(96.dp, 96.dp),
    titlePanelSize = DpSize(514.dp, 125.dp),
    titleFontSize = 52.sp,
    listTopSpacing = 61.dp,
    rowImageRes = R.drawable.img_panel_listitem,
    rowHeight = 186.dp,
    rowSpacing = 10.dp,
    rowPaddingStart = 48.dp,
    rowPaddingEnd = 72.dp,
    badgeSize = 114.dp,
    badgeFontSize = 48.sp,
    badgeSmallFontSize = 40.sp,
    nicknameStartSpacing = 36.dp,
    nicknameFontSize = 48.sp,
    scoreBelowNickname = false,
    scoreTopSpacing = 0.dp,
    scoreFontSize = 48.sp,
    myRankingHeight = 204.dp
)

// 뒤로 가기·제목은 도움말 세로 수치, 행 높이는 세로용 행 이미지 크기(픽셀 ÷ 3 = dp)다.
// 나머지는 시안 수치가 없어 정한 값이다. 1~4위가 다 보이고 5위 위쪽 끝만 보이게 맞췄다
private val PortraitDimens = LeaderboardDimens(
    backButtonSize = DpSize(84.dp, 84.dp),
    titlePanelSize = DpSize(421.dp, 104.dp),
    titleFontSize = 44.sp,
    listTopSpacing = 30.dp,
    rowImageRes = R.drawable.img_panel_listitem_portrait,
    rowHeight = 186.dp,
    rowSpacing = 4.dp,
    rowPaddingStart = 49.dp,
    rowPaddingEnd = 45.dp,
    badgeSize = 94.dp,
    badgeFontSize = 40.sp,
    badgeSmallFontSize = 32.sp,
    nicknameStartSpacing = 20.dp,
    nicknameFontSize = 36.sp,
    scoreBelowNickname = true,
    scoreTopSpacing = 14.dp,
    scoreFontSize = 40.sp,
    myRankingHeight = 170.dp
)

// 세로 화면 좌우 여백은 도움말 세로 화면과 같게 맞춘다
private val PortraitPaddingHorizontal = 60.dp

// 하단 '내 순위' 영역 아래 모서리. 화면 모서리와 같게 둥글린다
private val MyRankingCornerRadius = 40.dp

// 순위 배지. 아래 테두리를 두껍게 그려 입체 버튼처럼 보이게 한다
private val BadgeCornerRadius = 24.dp
private val BadgeBorderWidth = 4.dp
private val BadgeBottomBorderWidth = 10.dp
private val BadgeBrush = Brush.verticalGradient(listOf(ChaduckColors.Background.Normal, ChaduckColors.Primary.Light))

// 세 자리부터는 배지에 들어가게 글자를 줄인다
private const val BADGE_SMALL_FONT_RANK = 100

// 시스템 뒤로 가기도 뒤로 가기 버튼과 같다.
@Composable
fun LeaderboardScreen(contentPadding: PaddingValues, onBack: () -> Unit) {
    BackHandler(onBack = onBack)
    LeaderboardContent(
        uiState = SampleLeaderboardUiState,
        contentPadding = contentPadding,
        onBack = onBack
    )
}

// 배경은 AppContentFrame이 그린다. 위에 뒤로 가기 버튼과 화면 가운데 제목, 아래에 순위 리스트를 놓는다.
// 스크롤 없이 들어가는 행까지만 보이고, 리스트는 아래 여백 없이 화면 끝까지 그린다.
// 하단 '내 순위'는 공통 여백 중 좌우·아래를 무시하고 화면 끝에 붙어 리스트 위에 겹친다
@Composable
fun LeaderboardContent(
    uiState: LeaderboardUiState,
    contentPadding: PaddingValues,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isLandscape = currentWindowLayout() == WindowLayout.Landscape
    val dimens = if (isLandscape) LandscapeDimens else PortraitDimens
    val layoutDirection = LocalLayoutDirection.current
    val paddingStart = if (isLandscape) {
        contentPadding.calculateStartPadding(layoutDirection)
    } else {
        PortraitPaddingHorizontal.scaled
    }
    val paddingEnd = if (isLandscape) {
        contentPadding.calculateEndPadding(layoutDirection)
    } else {
        PortraitPaddingHorizontal.scaled
    }
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = paddingStart, top = contentPadding.calculateTopPadding(), end = paddingEnd)
        ) {
            TopBar(uiState, dimens, onBack)
            Spacer(Modifier.height(dimens.listTopSpacing.scaled))
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(dimens.rowSpacing.scaled),
                userScrollEnabled = false
            ) {
                items(uiState.rankings) { entry ->
                    RankingRow(entry, dimens)
                }
            }
        }
        uiState.myRanking?.let { entry ->
            MyRanking(
                entry = entry,
                dimens = dimens,
                screenPaddingStart = paddingStart,
                screenPaddingEnd = paddingEnd,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

// 제목은 화면 가운데다.
@Composable
private fun TopBar(uiState: LeaderboardUiState, dimens: LeaderboardDimens, onBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        BackButton(dimens.backButtonSize, onBack, Modifier.align(Alignment.CenterStart))
        TitlePanel(
            text = stringResource(R.string.leaderboard_title, uiState.month, uiState.week),
            size = dimens.titlePanelSize,
            fontSize = dimens.titleFontSize
        )
    }
}

// 행 배경 이미지는 행 크기에 맞춰 늘린다. 세로 화면은 모서리가 눌리지 않게 세로용 이미지를 쓴다
@Composable
private fun RankingRow(entry: LeaderboardEntry, dimens: LeaderboardDimens) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(dimens.rowHeight.scaled),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(dimens.rowImageRes),
            contentDescription = null,
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.FillBounds
        )
        RankingInfo(
            entry = entry,
            dimens = dimens,
            modifier = Modifier.padding(start = dimens.rowPaddingStart.scaled, end = dimens.rowPaddingEnd.scaled)
        )
    }
}

// 리스트 행과 같은 내용을 그림자·테두리 없이 흰 바탕에 놓는다.
// 영역은 좌우를 꽉 채우고, 글자는 화면 여백만큼 더 들여 리스트 행과 위치를 맞춘다.
@Composable
private fun MyRanking(
    entry: LeaderboardEntry,
    dimens: LeaderboardDimens,
    screenPaddingStart: Dp,
    screenPaddingEnd: Dp,
    modifier: Modifier = Modifier
) {
    val cornerRadius = MyRankingCornerRadius.scaled
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(dimens.myRankingHeight.scaled)
            .background(
                color = ChaduckColors.Background.Normal,
                shape = RoundedCornerShape(bottomStart = cornerRadius, bottomEnd = cornerRadius)
            ),
        contentAlignment = Alignment.Center
    ) {
        RankingInfo(
            entry = entry,
            dimens = dimens,
            modifier = Modifier.padding(
                start = screenPaddingStart + dimens.rowPaddingStart.scaled,
                end = screenPaddingEnd + dimens.rowPaddingEnd.scaled
            )
        )
    }
}

// [배지] [닉네임] ... [점수] 또는 [배지] [닉네임 / 점수]
@Composable
private fun RankingInfo(entry: LeaderboardEntry, dimens: LeaderboardDimens, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        RankBadge(entry.rank, dimens)
        Spacer(Modifier.width(dimens.nicknameStartSpacing.scaled))
        if (dimens.scoreBelowNickname) {
            Column(modifier = Modifier.weight(1f)) {
                Nickname(entry.nickname, dimens)
                Spacer(Modifier.height(dimens.scoreTopSpacing.scaled))
                Score(entry.score, dimens)
            }
        } else {
            Nickname(entry.nickname, dimens, Modifier.weight(1f))
            Score(entry.score, dimens)
        }
    }
}

@Composable
private fun RankBadge(rank: Int, dimens: LeaderboardDimens) {
    val fontSize = if (rank < BADGE_SMALL_FONT_RANK) dimens.badgeFontSize else dimens.badgeSmallFontSize
    val shape = RoundedCornerShape(BadgeCornerRadius.scaled)
    Box(
        modifier = Modifier
            .size(dimens.badgeSize.scaled)
            .background(ChaduckColors.Primary.Normal, shape)
            .padding(
                start = BadgeBorderWidth.scaled,
                top = BadgeBorderWidth.scaled,
                end = BadgeBorderWidth.scaled,
                bottom = BadgeBottomBorderWidth.scaled
            )
            .background(BadgeBrush, shape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = rank.toString(),
            style = MaterialTheme.typography.textTitle.copy(
                fontSize = fontSize,
                lineHeight = fontSize,
                textAlign = TextAlign.Center
            ).scaled(),
            color = ChaduckColors.Primary.Normal,
            maxLines = 1
        )
    }
}

@Composable
private fun Nickname(nickname: String, dimens: LeaderboardDimens, modifier: Modifier = Modifier) {
    Text(
        text = nickname,
        modifier = modifier,
        style = MaterialTheme.typography.textBold.copy(
            fontSize = dimens.nicknameFontSize,
            lineHeight = dimens.nicknameFontSize
        ).scaled(),
        color = ChaduckColors.Label.Normal,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun Score(score: Int, dimens: LeaderboardDimens) {
    Text(
        text = stringResource(R.string.leaderboard_score, score),
        style = MaterialTheme.typography.textTitle.copy(
            fontSize = dimens.scoreFontSize,
            lineHeight = dimens.scoreFontSize
        ).scaled(),
        color = ChaduckColors.Label.Normal,
        maxLines = 1
    )
}

// @PleosLandscapePreviews
@PleosPortraitPreviews
@Composable
private fun LeaderboardPreview() {
    val character = Character.Tofu
    ChaduckTheme {
        AppContentFrame(
            background = { CharacterBackground(character, Modifier.matchParentSize()) }
        ) { contentPadding ->
            LeaderboardContent(
                uiState = SampleLeaderboardUiState,
                contentPadding = contentPadding,
                onBack = {}
            )
        }
    }
}
