package com.yozmpeople.dontspill.ui.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.yozmpeople.dontspill.R
import com.yozmpeople.dontspill.common.PleosPreviews
import com.yozmpeople.dontspill.ui.character.Character
import com.yozmpeople.dontspill.ui.component.AppContentFrame
import com.yozmpeople.dontspill.ui.component.CharacterBackground
import com.yozmpeople.dontspill.ui.component.scaled
import com.yozmpeople.dontspill.ui.theme.ChaduckColors
import com.yozmpeople.dontspill.ui.theme.ChaduckTheme
import com.yozmpeople.dontspill.ui.theme.textTitle

// 글자 크기는 시안 수치가 없어 정한 값이다 (추정)
private val TitleFontSize = 96.sp

// 배경은 AppContentFrame이 그린다. 이 화면은 가운데 제목만 그린다.
@Composable
fun SplashContent(contentPadding: PaddingValues, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(contentPadding),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.splash_title),
            style = MaterialTheme.typography.textTitle.copy(fontSize = TitleFontSize).scaled(),
            color = ChaduckColors.Primary.Strong
        )
    }
}

@PleosPreviews
@Composable
private fun SplashContentPreview() {
    ChaduckTheme {
        AppContentFrame(
            background = { CharacterBackground(Character.WaterCup, Modifier.matchParentSize()) }
        ) { contentPadding ->
            SplashContent(contentPadding)
        }
    }
}
