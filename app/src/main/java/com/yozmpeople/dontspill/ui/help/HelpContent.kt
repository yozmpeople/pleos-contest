package com.yozmpeople.dontspill.ui.help

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
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
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yozmpeople.dontspill.R
import com.yozmpeople.dontspill.common.PleosLandscapePreviews
import com.yozmpeople.dontspill.common.PleosPreviews
import com.yozmpeople.dontspill.ui.character.Character
import com.yozmpeople.dontspill.ui.component.AppContentFrame
import com.yozmpeople.dontspill.ui.component.CharacterBackground
import com.yozmpeople.dontspill.ui.component.DesignScaleBox
import com.yozmpeople.dontspill.ui.component.ImageButton
import com.yozmpeople.dontspill.ui.component.ImagePanel
import com.yozmpeople.dontspill.ui.component.scaled
import com.yozmpeople.dontspill.ui.theme.ChaduckColors
import com.yozmpeople.dontspill.ui.theme.ChaduckTheme
import com.yozmpeople.dontspill.ui.theme.textBold
import com.yozmpeople.dontspill.ui.theme.textTitle

// 이미지 크기를 그대로 시안 크기로 쓴다 (픽셀 ÷ 3 = dp)
private val BackButtonSize = DpSize(96.dp, 96.dp)
private val TitlePanelSize = DpSize(514.dp, 125.dp)
private val CardSize = DpSize(1482.dp, 810.dp)
private val PageImageSize = DpSize(1307.dp, 440.dp)
private val ArrowButtonSize = DpSize(68.dp, 68.dp)

// 시안 수치 (사용자, 2026-11-11)
private val TitleFontSize = 52.sp
private val CardTopPadding = 70.dp
private val PageImageCornerRadius = 64.dp
private val TextTopSpacing = 54.dp
private val TextFontSize = 36.sp
private val PaginationTopSpacing = 38.dp
private val PaginationSpacing = 48.dp
private val PageNumberFontSize = 48.sp

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

// 배경은 AppContentFrame이 그린다. 위에 뒤로 가기 버튼과 제목, 남는 공간 가운데에 카드를 놓는다.
@Composable
fun HelpContent(
    page: HelpPage,
    contentPadding: PaddingValues,
    onBack: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(contentPadding)
    ) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            ImageButton(
                imageRes = R.drawable.img_button_back,
                contentDescription = stringResource(R.string.help_back),
                size = BackButtonSize,
                onClick = onBack,
                modifier = Modifier.align(Alignment.CenterStart)
            )
            ImagePanel(R.drawable.img_panel_title, TitlePanelSize) {
                Text(
                    text = stringResource(R.string.help_title),
                    style = MaterialTheme.typography.textTitle.copy(
                        fontSize = TitleFontSize,
                        lineHeight = TitleFontSize,
                        textAlign = TextAlign.Center
                    ).scaled(),
                    color = ChaduckColors.Primary.Normal
                )
            }
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            HelpCard(page, onPrevious, onNext)
        }
    }
}

// 카드 안은 카드 시안 크기로 배율을 다시 정한다. 세로 화면처럼 카드가 시안보다 좁아지면 안쪽도 같은 비율로 줄인다.
@Composable
private fun HelpCard(page: HelpPage, onPrevious: () -> Unit, onNext: () -> Unit) {
    DesignScaleBox(
        designSize = CardSize,
        modifier = Modifier
            .widthIn(max = CardSize.width.scaled)
            .aspectRatio(CardSize.width / CardSize.height)
    ) {
        ImagePanel(R.drawable.img_panel_card, CardSize, contentAlignment = Alignment.TopCenter) {
            Column(
                modifier = Modifier.padding(top = CardTopPadding.scaled),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(page.imageRes),
                    contentDescription = null,
                    modifier = Modifier
                        .size(PageImageSize.scaled)
                        .clip(RoundedCornerShape(PageImageCornerRadius.scaled)),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.height(TextTopSpacing.scaled))
                Text(
                    text = stringResource(page.textRes),
                    style = MaterialTheme.typography.textBold.copy(
                        fontSize = TextFontSize,
                        lineHeight = TextFontSize,
                        textAlign = TextAlign.Center
                    ).scaled(),
                    color = ChaduckColors.Label.Normal
                )
                Spacer(Modifier.height(PaginationTopSpacing.scaled))
                Pagination(page, onPrevious, onNext)
            }
        }
    }
}

// 첫·마지막 페이지에서는 없는 화살표 자리를 비워 두어 페이지 숫자가 가운데에 머물게 한다 (사용자, 2026-11-11)
@Composable
private fun Pagination(page: HelpPage, onPrevious: () -> Unit, onNext: () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(PaginationSpacing.scaled),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (page.previous() != null) {
            ImageButton(
                R.drawable.img_button_previous,
                stringResource(R.string.help_previous),
                ArrowButtonSize,
                onPrevious
            )
        } else {
            Spacer(Modifier.minimumInteractiveComponentSize().size(ArrowButtonSize.scaled))
        }
        Text(
            text = stringResource(R.string.help_page_number, page.number, HelpPage.entries.size),
            style = MaterialTheme.typography.textTitle.copy(
                fontSize = PageNumberFontSize,
                lineHeight = PageNumberFontSize,
                textAlign = TextAlign.Center
            ).scaled(),
            color = ChaduckColors.Primary.Normal
        )
        if (page.next() != null) {
            ImageButton(R.drawable.img_button_next, stringResource(R.string.help_next), ArrowButtonSize, onNext)
        } else {
            Spacer(Modifier.minimumInteractiveComponentSize().size(ArrowButtonSize.scaled))
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

@PleosLandscapePreviews
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
