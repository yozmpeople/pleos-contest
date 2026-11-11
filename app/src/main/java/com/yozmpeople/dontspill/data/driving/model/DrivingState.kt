package com.yozmpeople.dontspill.data.driving.model

// 주행 상태. 시각은 모두 부팅 후 경과시간(ms)이다.
sealed interface DrivingState {

    // 드라이빙 없음
    data object Idle : DrivingState

    // 주행 중. startedAtMs는 드라이빙 처음 움직이기 시작한 시각이다. 정차 후 이어가도 바뀌지 않는다.
    data class Driving(val startedAtMs: Long) : DrivingState

    // 정차 중. 5분 안에 다시 주행하면 같은 드라이빙을 이어간다.
    // stoppedAtMs는 정차가 시작된 시각이다. 드라이빙이 끝나면 이 시각을 종료 시각으로 쓴다.
    data class Stopped(val startedAtMs: Long, val stoppedAtMs: Long) : DrivingState
}
