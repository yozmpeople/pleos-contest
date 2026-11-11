package com.yozmpeople.dontspill.ui.result

import com.yozmpeople.dontspill.ui.character.Character
import java.util.Locale

// 결과 화면 상태. 실제 드라이빙 결과 연결은 게임 연결(09)에서 한다.
data class ResultUiState(
    val character: Character,
    val score: Int,
    val drivingSeconds: Long,
    val distanceMeters: Int
) {
    val grade: ResultGrade get() = ResultGrade.of(score)
}

// 주행 거리 표시. km 단위로 소수 첫째 자리까지 쓴다 (시안 예시 "1.7km")
fun formatDrivingDistance(distanceMeters: Int): String =
    String.format(Locale.ROOT, "%.1fkm", distanceMeters / 1000.0)
