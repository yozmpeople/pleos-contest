package com.yozmpeople.dontspill.ui.character

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface CharacterRepository {
    val selected: StateFlow<Character>

    fun select(character: Character)
}

// 고른 캐릭터는 배경처럼 모든 화면에서 쓴다. 앱을 다시 켜도 유지할지는 정하지 않아 메모리에만 둔다.
@Singleton
class DefaultCharacterRepository @Inject constructor() : CharacterRepository {

    private val _selected = MutableStateFlow(Character.entries.first())
    override val selected: StateFlow<Character> = _selected.asStateFlow()

    override fun select(character: Character) {
        _selected.value = character
    }
}
