package com.yozmpeople.dontspill.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize

// 이미지 한 장으로 된 버튼. size는 시안 값이고 배율을 곱해 쓴다.
// 배율로 작아져도 터치 영역은 최소 크기(48dp) 아래로 줄이지 않는다.
@Composable
fun ImageButton(
    @DrawableRes imageRes: Int,
    contentDescription: String,
    size: DpSize,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Image(
        painter = painterResource(imageRes),
        contentDescription = contentDescription,
        modifier = modifier
            .minimumInteractiveComponentSize()
            .pressScaleClickable(onClick = onClick)
            .size(size.scaled)
    )
}
