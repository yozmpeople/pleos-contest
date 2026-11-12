package com.yozmpeople.dontspill.ui.driving

import com.yozmpeople.dontspill.ui.character.Character
import java.util.Locale

// 주행 화면 상태. 실제 주행 시간 연결은 게임 연결(09)에서 한다.
data class DrivingUiState(
    val character: Character,
    val elapsedSeconds: Long
)

// 주행 시간 표시. 시:분:초를 두 자리씩 쓴다
fun formatDrivingTime(elapsedSeconds: Long): String {
    val hours = elapsedSeconds / 3600
    val minutes = elapsedSeconds % 3600 / 60
    val seconds = elapsedSeconds % 60
    return String.format(Locale.ROOT, "%02d:%02d:%02d", hours, minutes, seconds)
}
