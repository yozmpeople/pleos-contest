package com.yozmpeople.dontspill.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpSize
import com.yozmpeople.dontspill.R

// 화면 왼쪽 위 원형 뒤로 가기 버튼. size는 시안 값이다.
@Composable
fun BackButton(size: DpSize, onClick: () -> Unit, modifier: Modifier = Modifier) {
    ImageButton(
        imageRes = R.drawable.img_button_back,
        contentDescription = stringResource(R.string.back),
        size = size,
        onClick = onClick,
        modifier = modifier
    )
}
