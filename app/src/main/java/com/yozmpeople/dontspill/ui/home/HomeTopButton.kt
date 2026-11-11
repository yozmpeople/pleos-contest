package com.yozmpeople.dontspill.ui.home

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.yozmpeople.dontspill.R

// 홈 상단 버튼. 선언 순서대로 화면에 놓는다.
// 업적·주행 기록은 화면이 없어 버튼을 숨긴다. 화면을 만들면 주석을 푼다 (사용자, 2026-11-12)
enum class HomeTopButton(@DrawableRes val imageRes: Int, @StringRes val descriptionRes: Int) {
    // Achievement(R.drawable.img_button_achievement, R.string.home_achievement),
    Help(R.drawable.img_button_help, R.string.home_help),
    Leaderboard(R.drawable.img_button_leaderboard, R.string.home_leaderboard)
    // History(R.drawable.img_button_history, R.string.home_history)
}
