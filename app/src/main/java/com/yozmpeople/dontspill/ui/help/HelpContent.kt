package com.yozmpeople.dontspill.ui.help

import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yozmpeople.dontspill.R
import com.yozmpeople.dontspill.common.PleosPreviews
import com.yozmpeople.dontspill.ui.character.Character
import com.yozmpeople.dontspill.ui.component.AppContentFrame
import com.yozmpeople.dontspill.ui.component.CharacterBackground
import com.yozmpeople.dontspill.ui.component.DesignScaleBox
import com.yozmpeople.dontspill.ui.component.ImageButton
import com.yozmpeople.dontspill.ui.component.ImagePanel
import com.yozmpeople.dontspill.ui.component.WindowLayout
import com.yozmpeople.dontspill.ui.component.currentWindowLayout
import com.yozmpeople.dontspill.ui.component.scaled
import com.yozmpeople.dontspill.ui.theme.ChaduckColors
import com.yozmpeople.dontspill.ui.theme.ChaduckTheme
import com.yozmpeople.dontspill.ui.theme.textBold
import com.yozmpeople.dontspill.ui.theme.textTitle

// 가로·세로 시안에서 크기만 다른 요소의 수치
private class HelpDimens(
    val backButtonSize: DpSize,
    val titlePanelSize: DpSize,
    val titleFontSize: TextUnit,
    val cardTopSpacing: Dp,
    @DrawableRes val cardImageRes: Int,
    val cardSize: DpSize,
    val cardTopPadding: Dp,
    val cardBottomPadding: Dp,
    val pageImageSize: DpSize,
    val pageImageCornerRadius: Dp,
    val textTopSpacing: Dp,
    val textFontSize: TextUnit,
    val paginationTopSpacing: Dp,
    val arrowButtonSize: DpSize,
    val paginationSpacing: Dp,
    val pageNumberFontSize: TextUnit
)

// 이미지 크기를 그대로 시안 크기로 쓴다 (픽셀 ÷ 3 = dp). 나머지는 시안 수치 (사용자, 2026-11-11)
// 카드 아래 여백은 시안 간격을 다 놓고 남는 높이다.
private val LandscapeDimens = HelpDimens(
    backButtonSize = DpSize(96.dp, 96.dp),
    titlePanelSize = DpSize(514.dp, 125.dp),
    titleFontSize = 52.sp,
    cardTopSpacing = 0.dp,
    cardImageRes = R.drawable.img_panel_card,
    cardSize = DpSize(1482.dp, 810.dp),
    cardTopPadding = 70.dp,
    cardBottomPadding = 77.dp,
    pageImageSize = DpSize(1307.dp, 440.dp),
    pageImageCornerRadius = 64.dp,
    textTopSpacing = 54.dp,
    textFontSize = 36.sp,
    paginationTopSpacing = 40.dp,
    arrowButtonSize = DpSize(68.dp, 68.dp),
    paginationSpacing = 48.dp,
    pageNumberFontSize = 48.sp
)

// 카드와 페이지 이미지는 세로용 이미지 크기를 그대로 쓴다 (픽셀 ÷ 3 = dp).
// 나머지는 세로 시안 수치가 없어 정한 값이다 (추정)
private val PortraitDimens = HelpDimens(
    backButtonSize = DpSize(84.dp, 84.dp),
    titlePanelSize = DpSize(421.dp, 104.dp),
    titleFontSize = 44.sp,
    cardTopSpacing = 30.dp,
    cardImageRes = R.drawable.img_panel_card_portrait,
    cardSize = DpSize(694.dp, 938.dp),
    cardTopPadding = 70.dp,
    cardBottomPadding = 80.dp,
    pageImageSize = DpSize(554.dp, 440.dp),
    pageImageCornerRadius = 64.dp,
    textTopSpacing = 103.dp,
    textFontSize = 36.sp,
    paginationTopSpacing = 81.dp,
    arrowButtonSize = DpSize(54.dp, 54.dp),
    paginationSpacing = 48.dp,
    pageNumberFontSize = 40.sp
)

// 세로 화면은 공통 여백 대신 따로 정한 여백을 쓴다 (추정)
private val PortraitPaddingTop = 84.dp
private val PortraitPaddingBottom = 30.dp
private val PortraitPaddingHorizontal = 60.dp

// 페이지는 화면 회전에도 유지한다. 시스템 뒤로 가기도 뒤로 가기 버튼과 같다.
@Composable
fun HelpScreen(contentPadding: PaddingValues, onBack: () -> Unit) {
    var page by rememberSaveable { mutableStateOf(HelpPage.entries.first()) }
    BackHandler(onBack = onBack)
    HelpContent(
        page = page,
        contentPadding = contentPadding,
        onBack = onBack,
        onPrevious = { page.previous()?.let { page = it } },
        onNext = { page.next()?.let { page = it } }
    )
}

// 배경은 AppContentFrame이 그린다. 위에 뒤로 가기 버튼과 화면 가운데 제목, 남는 공간 가운데에 카드를 놓는다.
// 가로·세로는 여백과 수치, 페이지 이미지와 문구만 다르다.
@Composable
fun HelpContent(
    page: HelpPage,
    contentPadding: PaddingValues,
    onBack: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isLandscape = currentWindowLayout() == WindowLayout.Landscape
    val dimens = if (isLandscape) LandscapeDimens else PortraitDimens
    val padding = if (isLandscape) {
        contentPadding
    } else {
        PaddingValues(
            start = PortraitPaddingHorizontal.scaled,
            top = PortraitPaddingTop.scaled,
            end = PortraitPaddingHorizontal.scaled,
            bottom = PortraitPaddingBottom.scaled
        )
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            BackButton(dimens, onBack, Modifier.align(Alignment.CenterStart))
            TitlePanel(dimens)
        }
        Spacer(Modifier.height(dimens.cardTopSpacing.scaled))
        CardArea(
            page = page,
            imageRes = if (isLandscape) page.landscapeImageRes else page.portraitImageRes,
            textRes = if (isLandscape) page.textRes else page.portraitTextRes,
            dimens = dimens,
            onPrevious = onPrevious,
            onNext = onNext
        )
    }
}

@Composable
private fun BackButton(dimens: HelpDimens, onBack: () -> Unit, modifier: Modifier = Modifier) {
    ImageButton(
        imageRes = R.drawable.img_button_back,
        contentDescription = stringResource(R.string.help_back),
        size = dimens.backButtonSize,
        onClick = onBack,
        modifier = modifier
    )
}

@Composable
private fun TitlePanel(dimens: HelpDimens) {
    ImagePanel(R.drawable.img_panel_title, dimens.titlePanelSize) {
        Text(
            text = stringResource(R.string.help_title),
            style = MaterialTheme.typography.textTitle.copy(
                fontSize = dimens.titleFontSize,
                lineHeight = dimens.titleFontSize,
                textAlign = TextAlign.Center
            ).scaled(),
            color = ChaduckColors.Primary.Normal
        )
    }
}

// 남는 높이 가운데에 카드를 놓는다.
@Composable
private fun ColumnScope.CardArea(
    page: HelpPage,
    @DrawableRes imageRes: Int,
    @StringRes textRes: Int,
    dimens: HelpDimens,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        HelpCard(page, imageRes, textRes, dimens, onPrevious, onNext)
    }
}

// 카드 안은 카드 시안 크기로 배율을 다시 정한다. 카드가 시안보다 작아지면 안쪽도 같은 비율로 줄인다.
// 페이지네이션은 카드 아래쪽에 붙인다. 문구가 길어져도 문구와 최소 간격은 지킨다.
@Composable
private fun HelpCard(
    page: HelpPage,
    @DrawableRes imageRes: Int,
    @StringRes textRes: Int,
    dimens: HelpDimens,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    DesignScaleBox(
        designSize = dimens.cardSize,
        modifier = Modifier
            .widthIn(max = dimens.cardSize.width.scaled)
            .aspectRatio(dimens.cardSize.width / dimens.cardSize.height)
    ) {
        ImagePanel(dimens.cardImageRes, dimens.cardSize) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = dimens.cardTopPadding.scaled, bottom = dimens.cardBottomPadding.scaled),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(imageRes),
                    contentDescription = null,
                    modifier = Modifier
                        .size(dimens.pageImageSize.scaled)
                        .clip(RoundedCornerShape(dimens.pageImageCornerRadius.scaled)),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.height(dimens.textTopSpacing.scaled))
                Text(
                    text = stringResource(textRes),
                    style = MaterialTheme.typography.textBold.copy(
                        fontSize = dimens.textFontSize,
                        lineHeight = dimens.textFontSize,
                        textAlign = TextAlign.Center
                    ).scaled(),
                    color = ChaduckColors.Label.Normal
                )
                Spacer(Modifier.height(dimens.paginationTopSpacing.scaled))
                Spacer(Modifier.weight(1f))
                Pagination(page, dimens, onPrevious, onNext)
            }
        }
    }
}

// 첫·마지막 페이지에서는 없는 화살표 자리를 비워 두어 페이지 숫자가 가운데에 머물게 한다 (사용자, 2026-11-11)
@Composable
private fun Pagination(page: HelpPage, dimens: HelpDimens, onPrevious: () -> Unit, onNext: () -> Unit) {
    val arrowSize = dimens.arrowButtonSize
    Row(
        horizontalArrangement = Arrangement.spacedBy(dimens.paginationSpacing.scaled),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (page.previous() != null) {
            ImageButton(R.drawable.img_button_previous, stringResource(R.string.help_previous), arrowSize, onPrevious)
        } else {
            Spacer(Modifier.minimumInteractiveComponentSize().size(arrowSize.scaled))
        }
        Text(
            text = stringResource(R.string.help_page_number, page.number, HelpPage.entries.size),
            style = MaterialTheme.typography.textTitle.copy(
                fontSize = dimens.pageNumberFontSize,
                lineHeight = dimens.pageNumberFontSize,
                textAlign = TextAlign.Center
            ).scaled(),
            color = ChaduckColors.Primary.Normal
        )
        if (page.next() != null) {
            ImageButton(R.drawable.img_button_next, stringResource(R.string.help_next), arrowSize, onNext)
        } else {
            Spacer(Modifier.minimumInteractiveComponentSize().size(arrowSize.scaled))
        }
    }
}

@Composable
private fun HelpPreviewFrame(page: HelpPage) {
    val character = Character.Tofu
    ChaduckTheme {
        AppContentFrame(
            background = { CharacterBackground(character, Modifier.matchParentSize()) }
        ) { contentPadding ->
            HelpContent(
                page = page,
                contentPadding = contentPadding,
                onBack = {},
                onPrevious = {},
                onNext = {}
            )
        }
    }
}

@PleosPreviews
@Composable
private fun HelpSkinPreview() {
    HelpPreviewFrame(HelpPage.Skin)
}

// @PleosPreviews
// @Composable
// private fun HelpResultPreview() {
//    HelpPreviewFrame(HelpPage.Result)
// }
//
// @PleosPreviews
// @Composable
// private fun HelpLeaderboardPreview() {
//    HelpPreviewFrame(HelpPage.Leaderboard)
// }
