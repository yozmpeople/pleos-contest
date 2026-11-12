package com.yozmpeople.dontspill.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize

// 이미지 위에 글자를 얹는 패널. size는 시안 값이고 배율을 곱해 쓴다. 이미지는 패널 크기에 맞춰 늘린다.
// 글자는 안쪽 여백 없이 가운데에 놓는다. 예외는 트로피 자리가 있는 홈 점수 패널과 꼬리가 있는 홈 말풍선이다
@Composable
fun ImagePanel(
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
