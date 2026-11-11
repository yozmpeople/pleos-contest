package com.yozmpeople.dontspill.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yozmpeople.dontspill.common.ElapsedRealtimeClock
import com.yozmpeople.dontspill.data.driving.DrivingRepository
import com.yozmpeople.dontspill.data.driving.model.DrivingResult
import com.yozmpeople.dontspill.ui.character.Character
import com.yozmpeople.dontspill.ui.character.CharacterRepository
import com.yozmpeople.dontspill.ui.driving.DrivingUiState
import com.yozmpeople.dontspill.ui.result.ResultUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.math.roundToInt
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val STOP_TIMEOUT_MS = 5_000L
private const val TICK_MS = 1_000L

// 앱을 켰을 때 보일 화면을 주행 상태로 정한다 (사용자, 2026-11-11)
// 주행 중 → 주행 중 화면, 안 본 결과 → 결과 화면, 둘 다 아니면 사용자가 고른 화면이다.
// 백스택 없이 상태로 정하므로 결과 화면에서 끝난 주행 중 화면으로 돌아가지 않는다 (09 지시서)
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class AppViewModel @Inject constructor(
    private val drivingRepository: DrivingRepository,
    characterRepository: CharacterRepository,
    private val clock: ElapsedRealtimeClock
) : ViewModel() {

    // 주행 중이면 1초마다 주행 시간(초)을 낸다.
    private val drivingSeconds: Flow<Long?> = drivingRepository.drivingStartedAtMs.flatMapLatest { startedAtMs ->
        if (startedAtMs == null) {
            flowOf(null)
        } else {
            flow {
                while (true) {
                    emit((clock.nowMs() - startedAtMs).coerceAtLeast(0) / 1_000)
                    delay(TICK_MS)
                }
            }
        }
    }

    val uiState: StateFlow<AppUiState> = combine(
        drivingSeconds,
        drivingRepository.latestUnshownResult,
        characterRepository.selected
    ) { seconds, result, character ->
        when {
            seconds != null -> AppUiState.Driving(DrivingUiState(character, seconds))
            result != null -> AppUiState.Result(result.toUiState())
            else -> AppUiState.Normal
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
        initialValue = AppUiState.Normal
    )

    // 드라이빙을 끝내면 결과가 저장되고, 결과 화면으로 바뀐다.
    fun onEndDriving() {
        drivingRepository.endDriving()
    }

    fun onResultHome() {
        viewModelScope.launch { drivingRepository.markResultsShown() }
    }
}

// 저장된 캐릭터 이름을 못 찾으면 기본 캐릭터(물컵)로 보인다.
private fun DrivingResult.toUiState(): ResultUiState = ResultUiState(
    character = Character.entries.firstOrNull { it.name == character } ?: Character.WaterCup,
    score = score,
    drivingSeconds = durationMs / 1_000,
    distanceMeters = distanceMeters.roundToInt()
)
