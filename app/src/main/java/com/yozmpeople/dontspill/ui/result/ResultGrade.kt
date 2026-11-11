package com.yozmpeople.dontspill.ui.result

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.yozmpeople.dontspill.R
import com.yozmpeople.dontspill.ui.character.Character

// 결과 등급. 점수에 따라 위쪽 등급 로고와 캐릭터 상태가 바뀐다.
enum class ResultGrade(@DrawableRes val stickerRes: Int, @StringRes val descriptionRes: Int) {
    Perfect(R.drawable.img_sticker_perfect, R.string.result_grade_perfect),
    Good(R.drawable.img_sticker_good, R.string.result_grade_good),
    Bad(R.drawable.img_sticker_bad, R.string.result_grade_bad);

    @DrawableRes
    fun characterImageRes(character: Character): Int = when (this) {
        Perfect -> character.perfectImageRes
        Good -> character.goodImageRes
        Bad -> character.badImageRes
    }

    companion object {
        // 시안 예시는 99점 PERFECT, 75점 GOOD, 68점 BAD다. 경계 점수는 받지 못해 정한 값이다 (추정)
        private const val PERFECT_MIN_SCORE = 90
        private const val GOOD_MIN_SCORE = 70

        fun of(score: Int): ResultGrade = when {
            score >= PERFECT_MIN_SCORE -> Perfect
            score >= GOOD_MIN_SCORE -> Good
            else -> Bad
        }
    }
}
