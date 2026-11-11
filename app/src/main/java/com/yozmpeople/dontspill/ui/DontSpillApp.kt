package com.yozmpeople.dontspill.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yozmpeople.dontspill.ui.character.CharacterRepository
import com.yozmpeople.dontspill.ui.component.AppContentFrame
import com.yozmpeople.dontspill.ui.component.CharacterBackground
import com.yozmpeople.dontspill.ui.help.HelpScreen
import com.yozmpeople.dontspill.ui.home.HomeScreen
import com.yozmpeople.dontspill.ui.home.HomeTopButton
import com.yozmpeople.dontspill.ui.splash.SplashBackground
import com.yozmpeople.dontspill.ui.splash.SplashContent
import com.yozmpeople.dontspill.ui.theme.ChaduckTheme
import kotlinx.coroutines.delay

private const val SPLASH_DURATION_MS = 2_000L

// 스플래시 뒤에 보이는 화면
private enum class AppScreen { Home, Help }

// 앱 화면의 루트. 배경은 스플래시만 고정 이미지이고, 나머지 화면은 고른 캐릭터 기준으로 그린다.
// 화면이 더 늘면 Navigation 도입을 검토한다 (04 지시서 "미정")
@Composable
fun DontSpillApp(characterRepository: CharacterRepository) {
    val character by characterRepository.selected.collectAsStateWithLifecycle()
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
            if (showSplash) {
                SplashContent(contentPadding)
            } else {
                when (screen) {
                    // 도움말 말고 다른 상단 버튼의 화면은 아직 없다. 해당 화면을 만들 때 연결한다.
                    AppScreen.Home -> HomeScreen(
                        contentPadding = contentPadding,
                        onTopButtonClick = { button ->
                            if (button == HomeTopButton.Help) screen = AppScreen.Help
                        }
                    )

                    AppScreen.Help -> HelpScreen(
                        contentPadding = contentPadding,
                        onBack = { screen = AppScreen.Home }
                    )
                }
            }
        }
    }
}
