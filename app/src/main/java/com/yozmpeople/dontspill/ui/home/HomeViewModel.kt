package com.yozmpeople.dontspill.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yozmpeople.dontspill.ui.character.CharacterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

private const val STOP_TIMEOUT_MS = 5_000L

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val characterRepository: CharacterRepository
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = characterRepository.selected
        .map { HomeUiState.of(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
            initialValue = HomeUiState.of(characterRepository.selected.value)
        )

    fun onPrevious() {
        characterRepository.select(characterRepository.selected.value.previous())
    }

    fun onNext() {
        characterRepository.select(characterRepository.selected.value.next())
    }
}
