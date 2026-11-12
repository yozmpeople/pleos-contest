package com.yozmpeople.dontspill.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.yozmpeople.dontspill.R
import com.yozmpeople.dontspill.ui.theme.ChaduckColors
import com.yozmpeople.dontspill.ui.theme.textSemiBold

private val IconPadding = 1.dp
private val IconSpacing = 8.dp

// 화면 아래쪽의 정보(i) 아이콘 + 안내 문구. 크기는 시안 값이고 배율을 곱해 쓴다. 줄 높이는 글자 크기와 같다.
@Composable
fun InfoGuide(text: String, iconSize: Dp, fontSize: TextUnit, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(R.drawable.ic_circle_info),
            contentDescription = null,
            modifier = Modifier
                .padding(IconPadding.scaled)
                .size(iconSize.scaled)
        )
        Spacer(Modifier.width(IconSpacing.scaled))
        Text(
            text = text,
            style = MaterialTheme.typography.textSemiBold.copy(
                fontSize = fontSize,
                lineHeight = fontSize,
                textAlign = TextAlign.Center
            ).scaled(),
            color = ChaduckColors.Label.Normal
        )
    }
}
