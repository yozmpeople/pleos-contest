package com.yozmpeople.dontspill.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import com.yozmpeople.dontspill.R

// Asta Sans는 SemiBold만 쓴다. 가변 글꼴이라 weight는 Font를 고르는 기준일 뿐이고,
// wght 축 값은 variationSettings로 직접 넣어야 굵기가 바뀐다. 파일의 wght 범위는 300~800이다.
@OptIn(ExperimentalTextApi::class)
internal val AstaSans = FontFamily(
    Font(
        R.font.asta_sans_variable,
        weight = FontWeight.SemiBold,
        variationSettings = FontVariation.Settings(FontVariation.weight(FontWeight.SemiBold.weight))
    )
)

// Flight Sans는 Regular·Bold·Title 세 파일이다. Title 파일의 weight 값이 800이라 ExtraBold로 등록한다.
// 라이선스상 파일을 수정하거나 다른 형식으로 바꾸지 않는다.
internal val FlightSans = FontFamily(
    Font(R.font.flight_sans_regular, weight = FontWeight.Normal),
    Font(R.font.flight_sans_bold, weight = FontWeight.Bold),
    Font(R.font.flight_sans_title, weight = FontWeight.ExtraBold)
)

// 스타일을 지정하지 않은 Text와 Material 컴포넌트의 기본 글꼴을 Flight Sans로 맞춘다.
// 실제 사용할 텍스트 스타일은 아래 Typography.으로 확장 속성을 사용하여 정의한다.
internal val ChaduckTypography = Typography().run {
    copy(
        displayLarge = displayLarge.copy(fontFamily = FlightSans),
        displayMedium = displayMedium.copy(fontFamily = FlightSans),
        displaySmall = displaySmall.copy(fontFamily = FlightSans),
        headlineLarge = headlineLarge.copy(fontFamily = FlightSans),
        headlineMedium = headlineMedium.copy(fontFamily = FlightSans),
        headlineSmall = headlineSmall.copy(fontFamily = FlightSans),
        titleLarge = titleLarge.copy(fontFamily = FlightSans),
        titleMedium = titleMedium.copy(fontFamily = FlightSans),
        titleSmall = titleSmall.copy(fontFamily = FlightSans),
        bodyLarge = bodyLarge.copy(fontFamily = FlightSans),
        bodyMedium = bodyMedium.copy(fontFamily = FlightSans),
        bodySmall = bodySmall.copy(fontFamily = FlightSans),
        labelLarge = labelLarge.copy(fontFamily = FlightSans),
        labelMedium = labelMedium.copy(fontFamily = FlightSans),
        labelSmall = labelSmall.copy(fontFamily = FlightSans)
    )
}

// 크기·줄 높이·자간은 화면에서 시안 값으로 채운다.
// 예: MaterialTheme.typography.textBold.copy(fontSize = 24.sp, lineHeight = 36.sp, letterSpacing = 0.sp)
private val TextRegular = TextStyle(fontFamily = FlightSans, fontWeight = FontWeight.Normal)
private val TextSemiBold = TextStyle(fontFamily = AstaSans, fontWeight = FontWeight.SemiBold)
private val TextBold = TextStyle(fontFamily = FlightSans, fontWeight = FontWeight.Bold)
private val TextTitle = TextStyle(fontFamily = FlightSans, fontWeight = FontWeight.ExtraBold)

internal val Typography.textRegular: TextStyle get() = TextRegular
internal val Typography.textSemiBold: TextStyle get() = TextSemiBold
internal val Typography.textBold: TextStyle get() = TextBold
internal val Typography.textTitle: TextStyle get() = TextTitle
