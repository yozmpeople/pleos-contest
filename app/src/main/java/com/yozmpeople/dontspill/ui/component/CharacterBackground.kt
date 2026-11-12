package com.yozmpeople.dontspill.ui.component

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.yozmpeople.dontspill.ui.character.Character

// 캐릭터를 바꿀 때 배경이 겹치며 바뀌는 시간. 홈 캐릭터 전환과 같다
private const val BACKGROUND_TRANSITION_MS = 300

// 고른 캐릭터의 배경. 모든 화면에서 AppContentFrame의 background 칸에 그린다.
// 바뀌는 동안만 배경 두 장을 겹쳐 그린다.
@Composable
fun CharacterBackground(character: Character, modifier: Modifier = Modifier) {
    Crossfade(
        targetState = character,
        modifier = modifier,
        animationSpec = tween(BACKGROUND_TRANSITION_MS),
        label = "characterBackground"
    ) { character ->
        CharacterBackgroundImage(character)
    }
}

@Composable
private fun CharacterBackgroundImage(character: Character) {
    Image(
        painter = painterResource(character.backgroundRes),
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
}
