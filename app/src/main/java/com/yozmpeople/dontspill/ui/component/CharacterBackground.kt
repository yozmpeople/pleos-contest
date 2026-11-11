package com.yozmpeople.dontspill.ui.component

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.yozmpeople.dontspill.ui.character.Character

// 고른 캐릭터의 배경. 모든 화면에서 AppContentFrame의 background 칸에 그린다.
@Composable
fun CharacterBackground(character: Character, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(character.backgroundRes),
        contentDescription = null,
        modifier = modifier,
        contentScale = ContentScale.Crop
    )
}
