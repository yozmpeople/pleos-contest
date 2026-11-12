package com.yozmpeople.dontspill.ui

import com.yozmpeople.dontspill.MainDispatcherRule
import com.yozmpeople.dontspill.data.driving.DrivingRepository
import com.yozmpeople.dontspill.data.driving.model.DrivingResult
import com.yozmpeople.dontspill.ui.character.Character
import com.yozmpeople.dontspill.ui.character.DefaultCharacterRepository
import com.yozmpeople.dontspill.ui.driving.DrivingUiState
import com.yozmpeople.dontspill.ui.result.ResultUiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AppViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val driving = FakeDrivingRepository()
    private val characters = DefaultCharacterRepository()

    @Test
    fun `주행 중도 아니고 안 본 결과도 없으면 사용자가 고른 화면이다`() = runTest {
        val viewModel = startViewModel()

        assertEquals(AppUiState.Normal, viewModel.uiState.value)
    }

    @Test
    fun `주행 중이면 고른 캐릭터와 1초마다 늘어나는 주행 시간을 보인다`() = runTest {
        characters.select(Character.Tofu)
        driving.startedAtMs.value = 0
        val viewModel = startViewModel()

        advanceTimeBy(3_000)
        runCurrent()

        assertEquals(AppUiState.Driving(DrivingUiState(Character.Tofu, 3)), viewModel.uiState.value)
    }

    @Test
    fun `안 본 결과가 있으면 저장된 캐릭터와 결과를 보인다`() = runTest {
        driving.result.value = DrivingResult("id", "Laika", 75, durationMs = 754_900, distanceMeters = 1_700.4)
        val viewModel = startViewModel()

        assertEquals(
            AppUiState.Result(ResultUiState(Character.Laika, 75, drivingSeconds = 754, distanceMeters = 1_700)),
            viewModel.uiState.value
        )
    }

    @Test
    fun `주행 중이면 안 본 결과가 있어도 주행 중 화면이다`() = runTest {
        driving.startedAtMs.value = 0
        driving.result.value = DrivingResult("id", "Laika", 75, 1_000, 0.0)
        val viewModel = startViewModel()

        assertTrue(viewModel.uiState.value is AppUiState.Driving)
    }

    @Test
    fun `저장된 캐릭터 이름을 모르면 물컵으로 보인다`() = runTest {
        driving.result.value = DrivingResult("id", "Unknown", 75, 1_000, 0.0)
        val viewModel = startViewModel()

        val state = viewModel.uiState.value as AppUiState.Result
        assertEquals(Character.WaterCup, state.uiState.character)
    }

    @Test
    fun `종료를 누르면 드라이빙을 끝낸다`() = runTest {
        val viewModel = startViewModel()

        viewModel.onEndDriving()

        assertTrue(driving.ended)
    }

    @Test
    fun `결과에서 홈을 누르면 결과를 본 것으로 한다`() = runTest {
        driving.result.value = DrivingResult("id", "Laika", 75, 1_000, 0.0)
        val viewModel = startViewModel()

        viewModel.onResultHome()
        runCurrent()

        assertEquals(AppUiState.Normal, viewModel.uiState.value)
    }

    private fun TestScope.startViewModel(): AppViewModel {
        val viewModel = AppViewModel(driving, characters) { testScheduler.currentTime }
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
        runCurrent()
        return viewModel
    }
}

private class FakeDrivingRepository : DrivingRepository {
    val startedAtMs = MutableStateFlow<Long?>(null)
    val result = MutableStateFlow<DrivingResult?>(null)
    var ended = false

    override val drivingStartedAtMs = startedAtMs
    override val latestUnshownResult = result

    override fun endDriving() {
        ended = true
    }

    override suspend fun markResultsShown() {
        result.value = null
    }
}
