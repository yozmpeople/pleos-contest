package com.yozmpeople.dontspill.ui.theme

import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

internal object ChaduckColors {
    object Primary {
        val Normal = Color(0xFF7C5CFF)
        val Strong = Color(0xFF6933FF)
        val Light = Color(0xFFECE8FF)
    }

    object Label {
        val Normal = Color(0xFF202022)
        val Strong = Color(0xFF070708)
        val Neutral = Color(0xFF515158)
        val Alternative = Color(0xFF82828C)
        val Assistive = Color(0xFFB7B7BD)
        val Disabled = Color(0xFFD2D2D5)
    }

    object Status {
        val Positive = Color(0xFF00BF40)
        val Cautionary = Color(0xFFFF9200)
        val Destructive = Color(0xFFFF4242)
    }

    object Background {
        val Normal = Color(0xFFFFFFFF)
        val Surface = Color(0xFFF8F9FC)
        val Neutral = Color(0xFFF1F3F9)
        val Alternative = Color(0xFFECEEF3)
    }

    object Line {
        val Normal = Color(0xFFE7E7E9)
        val Alternative = Color(0xFFE1E1E4)
    }
}

// Material 컴포넌트가 기본 보라색 대신 앱 색을 쓰게 주요 역할만 매핑한다.
// 화면에서는 ChaduckColors를 직접 쓴다.
internal val LightColorScheme = lightColorScheme(
    primary = ChaduckColors.Primary.Normal,
    onPrimary = ChaduckColors.Background.Normal,
    primaryContainer = ChaduckColors.Primary.Light,
    background = ChaduckColors.Background.Normal,
    onBackground = ChaduckColors.Label.Normal,
    surface = ChaduckColors.Background.Normal,
    onSurface = ChaduckColors.Label.Normal,
    onSurfaceVariant = ChaduckColors.Label.Neutral,
    error = ChaduckColors.Status.Destructive,
    outline = ChaduckColors.Line.Normal,
    outlineVariant = ChaduckColors.Line.Alternative
)
