package com.yozmpeople.dontspill.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
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

// 화면 전환. 피그마 화면 전환 설정(디졸브, ease out, 300ms)
private const val SCREEN_TRANSITION_MS = 300
private val ScreenTransitionSpec: FiniteAnimationSpec<Float> = tween(SCREEN_TRANSITION_MS, easing = EaseOut)

// 스플래시 뒤에 사용자가 고르는 화면. 주행 중·결과 화면은 AppViewModel이 주행 상태로 정한다.
private enum class AppScreen { Home, Help, Leaderboard }

// 지금 보여줄 화면. 주행 중·결과는 값이 계속 바뀌므로 화면 종류(key)가 바뀔 때만 전환한다.
private sealed interface Destination {
    val key: Any

    data object Splash : Destination {
        override val key = this
    }

    data class Driving(val state: AppUiState.Driving) : Destination {
        override val key = Driving::class
    }

    data class Result(val state: AppUiState.Result) : Destination {
        override val key = Result::class
    }

    data class Picked(val screen: AppScreen) : Destination {
        override val key = screen
    }
}


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
    val state = appState
    val destination = when {
        showSplash -> Destination.Splash
        state is AppUiState.Driving -> Destination.Driving(state)
        state is AppUiState.Result -> Destination.Result(state)
        else -> Destination.Picked(screen)
    }

    ChaduckTheme {
        AppContentFrame(
            background = {
                Crossfade(
                    targetState = showSplash,
                    modifier = Modifier.matchParentSize(),
                    animationSpec = ScreenTransitionSpec,
                    label = "splashBackground"
                ) { splash ->
                    if (splash) {
                        SplashBackground(Modifier.fillMaxSize())
                    } else {
                        CharacterBackground(character, Modifier.fillMaxSize())
                    }
                }
            }
        ) { contentPadding ->
            AnimatedContent(
                targetState = destination,
                transitionSpec = { fadeIn(ScreenTransitionSpec) togetherWith fadeOut(ScreenTransitionSpec) },
                contentKey = { it.key },
                label = "screen"
            ) { target ->
                when (target) {
                    Destination.Splash -> SplashContent(contentPadding)

                    is Destination.Driving -> DrivingContent(
                        uiState = target.state.uiState,
                        contentPadding = contentPadding,
                        onEndDriving = viewModel::onEndDriving
                    )

                    is Destination.Result -> ResultContent(
                        uiState = target.state.uiState,
                        contentPadding = contentPadding,
                        onHome = {
                            screen = AppScreen.Home
                            viewModel.onResultHome()
                        }
                    )

                    is Destination.Picked -> when (target.screen) {
                        AppScreen.Home -> HomeScreen(
                            contentPadding = contentPadding,
                            onTopButtonClick = { button ->
                                when (button) {
                                    HomeTopButton.Help -> screen = AppScreen.Help
                                    HomeTopButton.Leaderboard -> screen = AppScreen.Leaderboard
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
}
