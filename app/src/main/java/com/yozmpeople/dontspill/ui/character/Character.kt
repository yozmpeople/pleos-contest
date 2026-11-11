package com.yozmpeople.dontspill.ui.character

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.yozmpeople.dontspill.R

// 홈에서 고르는 캐릭터. 선언 순서가 홈 화면의 좌우 순서다.
enum class Character(
    @StringRes val displayNameRes: Int,
    @StringRes val speechRes: Int,
    @DrawableRes val imageRes: Int,
    @DrawableRes val drivingImageRes: Int,
    @DrawableRes val backgroundRes: Int
) {
    WaterCup(
        R.string.character_water_cup_name,
        R.string.character_water_cup_speech,
        R.drawable.img_watercup_main,
        R.drawable.img_watercup_driving,
        R.drawable.img_skin_watercup
    ),
    Tofu(
        R.string.character_tofu_name,
        R.string.character_tofu_speech,
        R.drawable.img_tofu_main,
        R.drawable.img_tofu_driving,
        R.drawable.img_skin_tofu
    ),
    Sunfish(
        R.string.character_sunfish_name,
        R.string.character_sunfish_speech,
        R.drawable.img_sunfish_main,
        R.drawable.img_sunfish_driving,
        R.drawable.img_skin_sunfish
    ),
    Laika(
        R.string.character_laika_name,
        R.string.character_laika_speech,
        R.drawable.img_laika_main,
        R.drawable.img_laika_driving,
        R.drawable.img_skin_laika
    );

    // 끝에서 다음은 처음, 처음에서 이전은 끝이다.
    fun next(): Character = entries[(ordinal + 1) % entries.size]

    fun previous(): Character = entries[(ordinal - 1 + entries.size) % entries.size]
}
