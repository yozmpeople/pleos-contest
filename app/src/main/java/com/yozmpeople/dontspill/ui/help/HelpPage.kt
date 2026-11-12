package com.yozmpeople.dontspill.ui.help

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.yozmpeople.dontspill.R

// 도움말 페이지. 선언 순서가 페이지 순서다
// 세로 화면은 이미지 비율이 달라 이미지를 따로 쓰고, 문구는 시안 위치에서 줄을 바꾼 문자열을 쓴다.
enum class HelpPage(
    @DrawableRes val landscapeImageRes: Int,
    @DrawableRes val portraitImageRes: Int,
    @StringRes val textRes: Int,
    @StringRes val portraitTextRes: Int
) {
    Skin(
        R.drawable.img_help_skin_landscape,
        R.drawable.img_help_skin_portrait,
        R.string.help_skin,
        R.string.help_skin_portrait
    ),
    Result(
        R.drawable.img_help_result_landscape,
        R.drawable.img_help_result_portrait,
        R.string.help_result,
        R.string.help_result_portrait
    ),
    Leaderboard(
        R.drawable.img_help_leaderboard_landscape,
        R.drawable.img_help_leaderboard_portrait,
        R.string.help_leaderboard,
        R.string.help_leaderboard_portrait
    );

    // 화면에 보이는 페이지 번호. 1부터 센다.
    val number: Int get() = ordinal + 1

    // 첫 페이지의 이전, 마지막 페이지의 다음은 없다(순환하지 않는다).
    fun previous(): HelpPage? = entries.getOrNull(ordinal - 1)
    fun next(): HelpPage? = entries.getOrNull(ordinal + 1)
}
