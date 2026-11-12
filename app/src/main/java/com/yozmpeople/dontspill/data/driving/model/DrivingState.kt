package com.yozmpeople.dontspill.data.driving.model

// 주행 상태. 시각은 모두 부팅 후 경과시간(ms)이다.
sealed interface DrivingState {

    // 드라이빙 없음. 주행 중에 이 상태로 바뀐 시각(P 진입, 사용자 종료)을 종료 시각으로 쓴다.
    data object Idle : DrivingState

    // 주행 중. startedAtMs는 드라이빙 처음 움직이기 시작한 시각이다. 중간에 멈췄다 출발해도 바뀌지 않는다.
    data class Driving(val startedAtMs: Long) : DrivingState
}
