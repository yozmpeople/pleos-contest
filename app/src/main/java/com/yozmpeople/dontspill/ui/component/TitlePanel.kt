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

// 알약 모양 제목 패널(img_panel_title)에 굵은 보라 글씨를 얹는다. size와 fontSize는 시안 값이다.
@Composable
fun TitlePanel(text: String, size: DpSize, fontSize: TextUnit, modifier: Modifier = Modifier) {
    ImagePanel(R.drawable.img_panel_title, size, modifier) {
        Text(
            text = text,
            style = MaterialTheme.typography.textTitle.copy(
                fontSize = fontSize,
                lineHeight = fontSize,
                textAlign = TextAlign.Center
            ).scaled(),
            color = ChaduckColors.Primary.Normal
        )
    }
}
