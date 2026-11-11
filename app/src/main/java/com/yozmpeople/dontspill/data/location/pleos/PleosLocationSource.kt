package com.yozmpeople.dontspill.data.location.pleos

import ai.pleos.playground.location.fused.FusedLocation
import ai.pleos.playground.location.fused.listener.FusedLocationListener
import ai.pleos.playground.location.fused.model.LocationData
import android.content.Context
import com.yozmpeople.dontspill.common.debugLog
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

private const val TAG = "PleosLocationSource"

// Pleos FusedLocation SDK로 위치를 구독한다. SDK 값을 그대로 내보내고, 앱 값으로 바꾸는 일은 저장소에서 한다.
// 수신 주기를 정하는 API가 없다. 에뮬레이터에서는 약 1초마다 왔다 (차덕 검증 노트 3-16)
@Singleton
class PleosLocationSource @Inject constructor(@ApplicationContext context: Context) {

    // 서비스 연결 전 호출은 SDK가 쌓아 두었다가 연결 후 실행한다.
    private val fusedLocation = FusedLocation(context).apply { initialize() }

    // 리스너를 등록하고 마지막 위치도 한 번 읽는다. 마지막 위치가 리스너 값보다 늦게 오면 오래된 값이라 버린다.
    fun observeLocation(): Flow<LocationData> = callbackFlow {
        val updated = AtomicBoolean(false)
        val listener = object : FusedLocationListener {
            override fun onChanged(location: LocationData?) {
                if (location == null) return
                updated.set(true)
                trySend(location)
            }

            override fun onError(exception: Exception) = onFailed(exception)
        }
        // 권한이 없으면 SDK가 등록에서 SecurityException을 던진다. Flow를 끝내지 않고 값 없이 둔다.
        val registered = try {
            fusedLocation.registerFusedLocationCallback(listener)
            true
        } catch (e: SecurityException) {
            onFailed(e)
            false
        }
        if (registered) {
            fusedLocation.getLastKnownLocation(object : FusedLocationListener {
                override fun onChanged(location: LocationData?) {
                    if (location != null && !updated.get()) trySend(location)
                }

                override fun onError(exception: Exception) = onFailed(exception)
            })
        }
        awaitClose { if (registered) fusedLocation.unregisterFusedLocationCallback(listener) }
    }

    private fun onFailed(e: Exception) {
        debugLog(TAG) { "location failed ${e.javaClass.simpleName}" }
    }
}
