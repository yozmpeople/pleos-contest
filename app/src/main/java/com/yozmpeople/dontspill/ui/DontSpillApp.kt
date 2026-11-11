package com.yozmpeople.dontspill.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yozmpeople.dontspill.ui.character.CharacterRepository
import com.yozmpeople.dontspill.ui.component.AppContentFrame
import com.yozmpeople.dontspill.ui.component.CharacterBackground
import com.yozmpeople.dontspill.ui.driving.DrivingContent
import com.yozmpeople.dontspill.ui.help.HelpScreen
import com.yozmpeople.dontspill.ui.home.HomeScreen
import com.yozmpeople.dontspill.ui.home.HomeTopButton
import com.yozmpeople.dontspill.ui.leaderboard.LeaderboardScreen
import com.yozmpeople.dontspill.ui.result.ResultContent
import com.yozmpeople.dontspill.ui.splash.SplashBackground
import com.yozmpeople.dontspill.ui.splash.SplashContent
import com.yozmpeople.dontspill.ui.theme.ChaduckTheme
import kotlinx.coroutines.delay

private const val SPLASH_DURATION_MS = 2_000L

// 스플래시 뒤에 사용자가 고르는 화면. 주행 중·결과 화면은 AppViewModel이 주행 상태로 정한다.
private enum class AppScreen { Home, Help, Leaderboard }

// 앱 화면의 루트. 배경은 스플래시만 고정 이미지이고, 나머지 화면은 고른 캐릭터 기준으로 그린다.
// 결과 화면은 그 드라이빙의 캐릭터 배경을 쓴다.
// 화면 이동은 백스택 없이 enum과 주행 상태로 정한다 (09 지시서 "검토한 후보와 선택 이유")
@Composable
fun DontSpillApp(characterRepository: CharacterRepository, viewModel: AppViewModel = hiltViewModel()) {
    val selectedCharacter by characterRepository.selected.collectAsStateWithLifecycle()
    val appState by viewModel.uiState.collectAsStateWithLifecycle()
    val character = (appState as? AppUiState.Result)?.uiState?.character ?: selectedCharacter
    // 화면 회전 같은 재생성에서 스플래시를 다시 띄우지 않는다.
    var showSplash by rememberSaveable { mutableStateOf(true) }
    var screen by rememberSaveable { mutableStateOf(AppScreen.Home) }
    LaunchedEffect(Unit) {
        if (showSplash) {
            delay(SPLASH_DURATION_MS)
            showSplash = false
        }
    }

    ChaduckTheme {
        AppContentFrame(
            background = {
                if (showSplash) {
                    SplashBackground(Modifier.matchParentSize())
                } else {
                    CharacterBackground(character, Modifier.matchParentSize())
                }
            }
        ) { contentPadding ->
            val state = appState
            if (showSplash) {
                SplashContent(contentPadding)
            } else if (state is AppUiState.Driving) {
                DrivingContent(
                    uiState = state.uiState,
                    contentPadding = contentPadding,
                    onEndDriving = viewModel::onEndDriving
                )
            } else if (state is AppUiState.Result) {
                ResultContent(
                    uiState = state.uiState,
                    contentPadding = contentPadding,
                    onHome = {
                        screen = AppScreen.Home
                        viewModel.onResultHome()
                    }
                )
            } else {
                when (screen) {
                    // 업적·주행 기록 화면은 아직 없다. 해당 화면을 만들 때 연결한다.
                    AppScreen.Home -> HomeScreen(
                        contentPadding = contentPadding,
                        onTopButtonClick = { button ->
                            when (button) {
                                HomeTopButton.Help -> screen = AppScreen.Help
                                HomeTopButton.Leaderboard -> screen = AppScreen.Leaderboard
                                else -> Unit
                            }
                        }
                    )

                    AppScreen.Help -> HelpScreen(
                        contentPadding = contentPadding,
                        onBack = { screen = AppScreen.Home }
                    )

                    AppScreen.Leaderboard -> LeaderboardScreen(
                        contentPadding = contentPadding,
                        onBack = { screen = AppScreen.Home }
                    )
                }
            }
        }
    }
}
