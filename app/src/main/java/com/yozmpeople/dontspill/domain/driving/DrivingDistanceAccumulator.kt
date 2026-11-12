package com.yozmpeople.dontspill.domain.driving

import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

// 지구 평균 반지름(m)
private const val EARTH_RADIUS_M = 6_371_008.8

// 드라이빙 하나의 위치 점 사이 거리(m)를 이어서 더한다. 점은 걸러내지 않는다
// Android Location 없이 단위 테스트하려고 거리는 하버사인 공식으로 계산한다.
class DrivingDistanceAccumulator {
    // Float 거리를 계속 더하면 오차가 쌓여서 Double로 둔다.
    var totalMeters: Double = 0.0
        private set

    private var lastLatitudeDeg: Double? = null
    private var lastLongitudeDeg: Double? = null

    // 첫 점은 거리를 더하지 않는다.
    fun add(latitudeDeg: Double, longitudeDeg: Double) {
        val lastLatitude = lastLatitudeDeg
        val lastLongitude = lastLongitudeDeg
        if (lastLatitude != null && lastLongitude != null) {
            totalMeters += distanceMeters(lastLatitude, lastLongitude, latitudeDeg, longitudeDeg)
        }
        lastLatitudeDeg = latitudeDeg
        lastLongitudeDeg = longitudeDeg
    }
}

internal fun distanceMeters(fromLatDeg: Double, fromLonDeg: Double, toLatDeg: Double, toLonDeg: Double): Double {
    val fromLat = Math.toRadians(fromLatDeg)
    val toLat = Math.toRadians(toLatDeg)
    val dLat = toLat - fromLat
    val dLon = Math.toRadians(toLonDeg - fromLonDeg)
    val h = sin(dLat / 2).pow(2) + cos(fromLat) * cos(toLat) * sin(dLon / 2).pow(2)
    return 2 * EARTH_RADIUS_M * asin(sqrt(h.coerceIn(0.0, 1.0)))
}
