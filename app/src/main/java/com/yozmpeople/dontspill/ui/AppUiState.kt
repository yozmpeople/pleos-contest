package com.yozmpeople.dontspill.ui

import com.yozmpeople.dontspill.ui.driving.DrivingUiState
import com.yozmpeople.dontspill.ui.result.ResultUiState

// 주행 상태가 정하는 화면. Normal이면 사용자가 고른 화면(홈·도움말·리더보드)을 보인다.
sealed interface AppUiState {
    data object Normal : AppUiState

    data class Driving(val uiState: DrivingUiState) : AppUiState

    data class Result(val uiState: ResultUiState) : AppUiState
}
