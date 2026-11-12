package com.yozmpeople.dontspill.data.vehicle.pleos

import ai.pleos.playground.vehicle.Vehicle
import ai.pleos.playground.vehicle.api.model.area.WindowArea
import ai.pleos.playground.vehicle.constant.area.WindowValue
import ai.pleos.playground.vehicle.constant.control.LightState
import ai.pleos.playground.vehicle.constant.control.WiperState
import ai.pleos.playground.vehicle.listener.LightStateListener
import ai.pleos.playground.vehicle.listener.SteeringWheelAngleListener
import ai.pleos.playground.vehicle.listener.WiperStateListener
import android.content.Context
import android.os.SystemClock
import com.yozmpeople.dontspill.common.debugLog
import com.yozmpeople.dontspill.data.vehicle.model.VehicleReading
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

private const val TAG = "PleosVehicleSource"
private val FRONT_WINDSHIELD = WindowArea(WindowValue.FRONT_WINDSHIELD)

// Pleos Vehicle SDK로 하향등·상향등·전방 와이퍼·전륜각을 구독한다.
// SDK 값을 그대로 내보내고, 앱 값으로 바꾸는 일은 저장소에서 한다.
// 콜백에 측정 시각이 없어서 받은 순간의 SystemClock.elapsedRealtime()을 붙인다.
@Singleton
class PleosVehicleSource @Inject constructor(@ApplicationContext context: Context) {

    // 서비스 연결 전 호출은 SDK가 쌓아 두었다가 연결 후 실행하고, 재연결 때 리스너를 다시 등록한다.
    private val vehicle = Vehicle(context).apply { initialize() }
    private val light = vehicle.getLight()
    private val wiper = vehicle.getWiper()
    private val steeringWheel = vehicle.getSteeringWheel()

    fun observeLowBeam(): Flow<VehicleReading<LightState?>> = observeState(
        name = "lowBeam",
        listenerOf = ::lightStateListener,
        register = light::registerLowBeamLightState,
        unregister = light::unregisterLowBeamLightState,
        readCurrent = light::getCurrentLowBeamLightState
    )

    fun observeHighBeam(): Flow<VehicleReading<LightState?>> = observeState(
        name = "highBeam",
        listenerOf = ::lightStateListener,
        register = light::registerHighBeamLightState,
        unregister = light::unregisterHighBeamLightState,
        readCurrent = light::getCurrentHighBeamLightState
    )

    // 스위치 위치가 아니라 실제 작동 상태(WiperState)
    fun observeFrontWiper(): Flow<VehicleReading<WiperState?>> = observeState(
        name = "frontWiper",
        listenerOf = ::wiperStateListener,
        register = { wiper.registerWiperState(FRONT_WINDSHIELD, it) },
        unregister = { wiper.unregisterWiperState(FRONT_WINDSHIELD, it) },
        readCurrent = { onComplete, onFailed -> wiper.getCurrentWiperState(FRONT_WINDSHIELD, onComplete, onFailed) }
    )

    // 전륜 조향각(도). 좌회전이 음수. 핸들 회전각이 아니다.
    fun observeFrontWheelAngle(): Flow<VehicleReading<Float?>> = observeState(
        name = "frontWheelAngle",
        listenerOf = ::steeringWheelAngleListener,
        register = steeringWheel::registerSteeringWheelAngle,
        unregister = steeringWheel::unregisterSteeringWheelAngle,
        readCurrent = steeringWheel::getCurrentSteeringWheelAngle
    )

    // 리스너를 등록하고 현재 값도 한 번 읽는다. 등록 직후 현재 값이 오는지 확인하지 못했다.
    // 현재 값 응답이 리스너 값보다 늦게 오면 오래된 값이라 버린다.
    private fun <T, L> observeState(
        name: String,
        listenerOf: (onUpdate: (T?) -> Unit, onFailed: (Exception) -> Unit) -> L,
        register: (L) -> Unit,
        unregister: (L) -> Unit,
        readCurrent: (onComplete: (T?) -> Unit, onFailed: (Exception) -> Unit) -> Unit
    ): Flow<VehicleReading<T?>> = callbackFlow {
        val updated = AtomicBoolean(false)
        val onFailed = { e: Exception -> debugLog(TAG) { "$name failed ${e.javaClass.simpleName}" } }
        val listener = listenerOf(
            { value ->
                updated.set(true)
                trySend(VehicleReading(SystemClock.elapsedRealtime(), value))
            },
            onFailed
        )
        // 권한이 없으면 SDK가 등록에서 예외를 던진다. Flow를 끝내지 않고 값 없이 둔다.
        val registered = try {
            register(listener)
            true
        } catch (e: Exception) {
            onFailed(e)
            false
        }
        if (registered) {
            readCurrent(
                { value -> if (!updated.get()) trySend(VehicleReading(SystemClock.elapsedRealtime(), value)) },
                onFailed
            )
        }
        awaitClose { if (registered) unregister(listener) }
    }
}

private fun lightStateListener(onUpdate: (LightState?) -> Unit, onFailed: (Exception) -> Unit) =
    object : LightStateListener {
        override fun onLightStateUpdated(state: LightState?) = onUpdate(state)
        override fun onFailed(e: Exception) = onFailed(e)
    }

private fun wiperStateListener(onUpdate: (WiperState?) -> Unit, onFailed: (Exception) -> Unit) =
    object : WiperStateListener {
        override fun onWiperStateUpdated(state: WiperState?) = onUpdate(state)
        override fun onFailed(e: Exception) = onFailed(e)
    }

private fun steeringWheelAngleListener(onUpdate: (Float?) -> Unit, onFailed: (Exception) -> Unit) =
    object : SteeringWheelAngleListener {
        override fun onSteeringWheelAngleUpdated(angle: Float?) = onUpdate(angle)
        override fun onFailed(e: Exception) = onFailed(e)
    }
