package com.yozmpeople.dontspill.ui.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.TextUnit
import com.yozmpeople.dontspill.R
import com.yozmpeople.dontspill.ui.theme.ChaduckColors
import com.yozmpeople.dontspill.ui.theme.textTitle

// 보라 그라데이션 알약 버튼(img_panel_button)에 흰 굵은 글씨를 얹는다. size와 fontSize는 시안 값이다.
@Composable
fun PillButton(text: String, size: DpSize, fontSize: TextUnit, onClick: () -> Unit, modifier: Modifier = Modifier) {
    ImagePanel(
        imageRes = R.drawable.img_panel_button,
        size = size,
        modifier = modifier.pressScaleClickable(onClick = onClick)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.textTitle.copy(
                fontSize = fontSize,
                lineHeight = fontSize,
                textAlign = TextAlign.Center
            ).scaled(),
            color = ChaduckColors.Background.Normal
        )
    }
}
