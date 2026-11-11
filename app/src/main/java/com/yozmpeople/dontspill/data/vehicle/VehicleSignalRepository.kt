package com.yozmpeople.dontspill.data.vehicle

import com.yozmpeople.dontspill.common.ApplicationScope
import com.yozmpeople.dontspill.data.vehicle.aaos.CarPropertySource
import com.yozmpeople.dontspill.data.vehicle.model.Gear
import com.yozmpeople.dontspill.data.vehicle.model.IgnitionState
import com.yozmpeople.dontspill.data.vehicle.model.VehicleReading
import com.yozmpeople.dontspill.data.vehicle.pleos.NaviSpeedLimitSource
import com.yozmpeople.dontspill.data.vehicle.pleos.PleosVehicleSource
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn

// 마지막 구독이 끝나도 잠깐은 SDK 등록을 유지한다. 화면 회전 같은 짧은 재구독에 다시 등록하지 않으려고서다.
private const val STOP_TIMEOUT_MS = 5_000L

// 차량 신호를 앱 값으로 내보낸다. 값마다 측정 시각(부팅 후 경과시간, ms)이 붙는다.
interface VehicleSignalRepository {
    // 부호 있는 차속(m/s). 후진이면 음수
    val speedMps: Flow<VehicleReading<Double>>
    val gear: Flow<VehicleReading<Gear>>
    val ignition: Flow<VehicleReading<IgnitionState>>
    val lowBeamOn: Flow<VehicleReading<Boolean?>>
    val highBeamOn: Flow<VehicleReading<Boolean?>>
    val frontWipersOn: Flow<VehicleReading<Boolean?>>

    // 전륜 조향각(도). 좌회전이 음수
    val frontWheelAngleDeg: Flow<VehicleReading<Double?>>
    val speedLimitKmh: Flow<VehicleReading<Double?>>

    // 축거(m). 드라이빙 시작 때 한 번 읽는다.
    suspend fun readWheelbaseM(): Double?
}

// 여러 곳에서 구독해도 SDK에는 신호마다 한 번만 등록한다. 새 구독자는 마지막 값을 바로 받는다.
@Singleton
class DefaultVehicleSignalRepository @Inject constructor(
    private val carPropertySource: CarPropertySource,
    pleosVehicleSource: PleosVehicleSource,
    naviSpeedLimitSource: NaviSpeedLimitSource,
    @ApplicationScope private val scope: CoroutineScope
) : VehicleSignalRepository {

    override val speedMps = carPropertySource.observeSpeed().mapValue(VehicleValueMapper::speedMps)
    override val gear = carPropertySource.observeGear().mapValue(VehicleValueMapper::gear)
    override val ignition = carPropertySource.observeIgnition().mapValue(VehicleValueMapper::ignition)
    override val lowBeamOn = pleosVehicleSource.observeLowBeam().mapValue(VehicleValueMapper::lightOn)
    override val highBeamOn = pleosVehicleSource.observeHighBeam().mapValue(VehicleValueMapper::lightOn)
    override val frontWipersOn = pleosVehicleSource.observeFrontWiper().mapValue(VehicleValueMapper::wiperOn)
    override val frontWheelAngleDeg =
        pleosVehicleSource.observeFrontWheelAngle().mapValue(VehicleValueMapper::frontWheelAngleDeg)
    override val speedLimitKmh = naviSpeedLimitSource.observeSpeedLimit().mapValue(VehicleValueMapper::speedLimitKmh)

    override suspend fun readWheelbaseM(): Double? =
        VehicleValueMapper.wheelbaseM(carPropertySource.readExteriorDimensions())

    private fun <T, R> Flow<VehicleReading<T>>.mapValue(transform: (T) -> R): Flow<VehicleReading<R>> =
        map { VehicleReading(it.timestampMs, transform(it.value)) }
            .shareIn(scope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), replay = 1)
}
