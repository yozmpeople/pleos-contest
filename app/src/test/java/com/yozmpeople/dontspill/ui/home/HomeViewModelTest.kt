package com.yozmpeople.dontspill.ui.home

import com.yozmpeople.dontspill.MainDispatcherRule
import com.yozmpeople.dontspill.ui.character.Character
import com.yozmpeople.dontspill.ui.character.DefaultCharacterRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = DefaultCharacterRepository()

    @Test
    fun `처음에는 첫 캐릭터가 가운데이고 양옆은 마지막과 두 번째 캐릭터다`() {
        // when
        val viewModel = HomeViewModel(repository)

        // then
        val state = viewModel.uiState.value
        assertEquals(Character.Laika, state.previousCharacter)
        assertEquals(Character.WaterCup, state.character)
        assertEquals(Character.Tofu, state.nextCharacter)
        assertEquals(0, state.totalScore)
    }

    @Test
    fun `다음을 누르면 다음 캐릭터를 고르고 화면 상태도 바뀐다`() = runTest {
        // given
        val viewModel = HomeViewModel(repository)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

        // when
        viewModel.onNext()

        // then
        assertEquals(Character.Tofu, repository.selected.value)
        assertEquals(Character.Tofu, viewModel.uiState.value.character)
        assertEquals(Character.WaterCup, viewModel.uiState.value.previousCharacter)
        assertEquals(Character.Sunfish, viewModel.uiState.value.nextCharacter)
    }

    @Test
    fun `처음 캐릭터에서 이전을 누르면 마지막 캐릭터를 고른다`() = runTest {
        // given
        val viewModel = HomeViewModel(repository)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

        // when
        viewModel.onPrevious()

        // then
        assertEquals(Character.Laika, viewModel.uiState.value.character)
    }

    @Test
    fun `마지막 캐릭터에서 다음을 누르면 처음 캐릭터를 고른다`() = runTest {
        // given
        repository.select(Character.Laika)
        val viewModel = HomeViewModel(repository)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

        // when
        viewModel.onNext()

        // then
        assertEquals(Character.WaterCup, viewModel.uiState.value.character)
    }
}
