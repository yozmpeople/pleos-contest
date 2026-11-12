package com.yozmpeople.dontspill.data.vehicle.aaos

import android.car.Car
import android.car.VehiclePropertyIds
import android.car.hardware.CarPropertyValue
import android.car.hardware.property.CarPropertyManager
import android.content.Context
import com.yozmpeople.dontspill.common.debugLog
import com.yozmpeople.dontspill.data.vehicle.model.VehicleReading
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.withTimeoutOrNull

private const val TAG = "CarPropertySource"
private const val GLOBAL_AREA_ID = 0
private val CONNECT_TIMEOUT = 10.seconds

// AAOS CarPropertyManager로 차속·기어를 구독하고, 차량 외형 치수를 한 번 읽는다.
// SDK 값을 그대로 내보내고, 앱 값으로 바꾸는 일은 저장소에서 한다.
@OptIn(ExperimentalCoroutinesApi::class)
@Singleton
class CarPropertySource @Inject constructor(@ApplicationContext context: Context) {

    // 연결되면 CarPropertyManager, 연결 전이거나 끊기면 null
    private val propertyManager = MutableStateFlow<CarPropertyManager?>(null)

    init {
        // 기다리지 않고 만들고, 연결·재연결·끊김은 리스너로 받는다.
        Car.createCar(context, null, Car.CAR_WAIT_TIMEOUT_DO_NOT_WAIT) { car, ready ->
            debugLog(TAG) { "car service ready=$ready" }
            propertyManager.value = if (ready) car.getCarManager(Car.PROPERTY_SERVICE) as? CarPropertyManager else null
        }
    }

    // 부호 있는 차속(m/s). 후진이면 음수
    fun observeSpeed(): Flow<VehicleReading<Float>> =
        observe(VehiclePropertyIds.PERF_VEHICLE_SPEED, CarPropertyManager.SENSOR_RATE_UI).castValues()

    // VehicleGear 값
    fun observeGear(): Flow<VehicleReading<Int>> =
        observe(VehiclePropertyIds.GEAR_SELECTION, CarPropertyManager.SENSOR_RATE_ONCHANGE).castValues()

    // INFO_EXTERIOR_DIMENSIONS 배열(mm). 연결되지 않았거나 읽지 못하면 null
    suspend fun readExteriorDimensions(): IntArray? {
        val manager = awaitPropertyManager() ?: return null
        val value = manager.readOrNull(VehiclePropertyIds.INFO_EXTERIOR_DIMENSIONS) ?: return null
        return (value as? Array<*>)?.map { it as? Int ?: return null }?.toIntArray()
    }

    // 연결이 바뀌면 새 CarPropertyManager로 다시 구독한다.
    private fun observe(propertyId: Int, rate: Float): Flow<VehicleReading<Any?>> =
        propertyManager.flatMapLatest { manager -> manager?.events(propertyId, rate) ?: emptyFlow() }

    private fun CarPropertyManager.events(propertyId: Int, rate: Float): Flow<VehicleReading<Any?>> = callbackFlow {
        val callback = object : CarPropertyManager.CarPropertyEventCallback {
            override fun onChangeEvent(value: CarPropertyValue<*>) {
                if (value.isAvailable()) trySend(VehicleReading(value.timestampMs(), value.value))
            }

            override fun onErrorEvent(propId: Int, zone: Int) {
                debugLog(TAG) { "error event property=$propId" }
            }
        }

        // 설정이 없으면 지원하지 않거나 권한이 없다.
        // API 35 미만 기기에는 subscribePropertyEvents()가 없어서 deprecated된 registerCallback()을 쓴다.
        @Suppress("DEPRECATION")
        val registered = getCarPropertyConfig(propertyId) != null && registerCallback(callback, propertyId, rate)
        if (!registered) debugLog(TAG) { "not registered property=$propertyId" }

        @Suppress("DEPRECATION")
        awaitClose { if (registered) unregisterCallback(callback, propertyId) }
    }

    private fun CarPropertyManager.readOrNull(propertyId: Int): Any? = try {
        getProperty<Any>(propertyId, GLOBAL_AREA_ID)?.takeIf { it.isAvailable() }?.value
    } catch (e: Exception) {
        // 지원하지 않음, 권한 없음, 값 없음이 모두 예외로 온다.
        debugLog(TAG) { "read failed property=$propertyId ${e.javaClass.simpleName}" }
        null
    }

    private suspend fun awaitPropertyManager(): CarPropertyManager? =
        withTimeoutOrNull(CONNECT_TIMEOUT) { propertyManager.filterNotNull().first() }
}

// CarPropertyValue.timestamp는 부팅 후 경과시간(ns)이다. elapsedRealtimeNanos와 같은 기준으로 본다.
private fun CarPropertyValue<*>.timestampMs(): Long = timestamp.nanoseconds.inWholeMilliseconds

// API 35 미만 기기에는 getPropertyStatus()가 없어서 deprecated된 getStatus()를 쓴다.
@Suppress("DEPRECATION")
private fun CarPropertyValue<*>.isAvailable(): Boolean = status == CarPropertyValue.STATUS_AVAILABLE

private inline fun <reified T> Flow<VehicleReading<Any?>>.castValues(): Flow<VehicleReading<T>> =
    mapNotNull { raw -> (raw.value as? T)?.let { VehicleReading(raw.timestampMs, it) } }
