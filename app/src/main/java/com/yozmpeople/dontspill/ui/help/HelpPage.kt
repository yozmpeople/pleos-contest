package com.yozmpeople.dontspill.ui.help

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.yozmpeople.dontspill.R

// 도움말 페이지. 선언 순서가 페이지 순서다 (사용자, 2026-11-11)
enum class HelpPage(
    @DrawableRes val imageRes: Int,
    @StringRes val textRes: Int
) {
    Skin(R.drawable.img_help_skin, R.string.help_skin),
    Result(R.drawable.img_help_result, R.string.help_result),
    Leaderboard(R.drawable.img_help_leaderboard, R.string.help_leaderboard);

    // 화면에 보이는 페이지 번호. 1부터 센다.
    val number: Int get() = ordinal + 1

    // 첫 페이지의 이전, 마지막 페이지의 다음은 없다(순환하지 않는다).
    fun previous(): HelpPage? = entries.getOrNull(ordinal - 1)
    fun next(): HelpPage? = entries.getOrNull(ordinal + 1)
}
