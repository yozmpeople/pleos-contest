package com.yozmpeople.dontspill.ui.home

import com.yozmpeople.dontspill.ui.character.Character

// 홈 화면에는 고른 캐릭터를 가운데에, 이전·다음 캐릭터를 양옆에 그린다.
data class HomeUiState(
    val previousCharacter: Character,
    val character: Character,
    val nextCharacter: Character,
    val totalScore: Int
) {
    companion object {
        // 누적 점수는 아직 정의하지 않아 0으로 둔다.
        fun of(character: Character, totalScore: Int = 0) = HomeUiState(
            previousCharacter = character.previous(),
            character = character,
            nextCharacter = character.next(),
            totalScore = totalScore
        )
    }
}
