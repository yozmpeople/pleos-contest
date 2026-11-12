package com.yozmpeople.dontspill.common

// 부팅 후 경과시간(ms). SystemClock.elapsedRealtime()과 같은 기준이다. 테스트에서 가짜 시계로 바꾼다.
fun interface ElapsedRealtimeClock {
    fun nowMs(): Long
}
