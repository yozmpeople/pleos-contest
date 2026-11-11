package com.yozmpeople.dontspill.data.vehicle.pleos

import ai.pleos.playground.navi.helper.NaviHelper
import ai.pleos.playground.navi.helper.listener.NaviHelperEventListener
import android.content.Context
import android.os.SystemClock
import com.yozmpeople.dontspill.data.vehicle.model.VehicleReading
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

// Pleos NaviHelper로 현재 도로 제한속도를 구독한다.
// SDK 값을 그대로 내보내고, 앱 값으로 바꾸는 일은 저장소에서 한다.
// 콜백에 측정 시각이 없어서 받은 순간의 SystemClock.elapsedRealtime()을 붙인다.
@Singleton
class NaviSpeedLimitSource @Inject constructor(@ApplicationContext context: Context) {

    // 서비스에 연결되면 SDK가 이벤트 콜백을 등록하고, 재연결 때 다시 등록한다.
    private val naviHelper = NaviHelper(context).apply { initialize() }

    // SDK 주석상 단위는 "(km)"이고 리스너 등록 뒤 1초마다 온다. 제한이 없는 도로의 값은 확인하지 못했다.
    fun observeSpeedLimit(): Flow<VehicleReading<Int>> = callbackFlow {
        val listener = object : NaviHelperEventListener {
            override fun onCurrentRoadSpeedLimit(speedLimit: Int) {
                trySend(VehicleReading(SystemClock.elapsedRealtime(), speedLimit))
            }
        }
        naviHelper.addListener(listener)
        awaitClose { naviHelper.removeListener(listener) }
    }
}
