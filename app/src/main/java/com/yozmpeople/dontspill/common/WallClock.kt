package com.yozmpeople.dontspill.common

// 실제 시각(epoch ms). System.currentTimeMillis()와 같은 기준이다. 테스트에서 가짜 시계로 바꾼다.
fun interface WallClock {
    fun nowMs(): Long
}
