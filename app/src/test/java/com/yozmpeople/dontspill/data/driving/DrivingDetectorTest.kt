package com.yozmpeople.dontspill.data.driving

import com.yozmpeople.dontspill.data.driving.model.DrivingState
import com.yozmpeople.dontspill.data.vehicle.VehicleSignalRepository
import com.yozmpeople.dontspill.data.vehicle.model.Gear
import com.yozmpeople.dontspill.data.vehicle.model.IgnitionState
import com.yozmpeople.dontspill.data.vehicle.model.VehicleReading
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DrivingDetectorTest {

    private val vehicle = FakeVehicleSignalRepository()

    @Test
    fun `움직임이 5초 이어지면 주행이고 시작 시각은 움직이기 시작한 시각이다`() = runTest {
        val detector = startDetector()

        vehicle.move()
        waitMs(4_999)
        assertEquals(DrivingState.Idle, detector.state.value)

        waitMs(1)
        assertEquals(DrivingState.Driving(startedAtMs = 0), detector.state.value)
    }

    @Test
    fun `5초가 되기 전에 멈추면 주행이 아니다`() = runTest {
        val detector = startDetector()

        vehicle.move()
        waitMs(3_000)
        vehicle.stop()
        waitMs(10_000)

        assertEquals(DrivingState.Idle, detector.state.value)
    }

    @Test
    fun `P에서는 차속이 있어도 주행이 아니다`() = runTest {
        val detector = startDetector()

        vehicle.gear(Gear.PARK)
        vehicle.speed(5.0)
        waitMs(10_000)

        assertEquals(DrivingState.Idle, detector.state.value)
    }

    @Test
    fun `시동과 기어 값이 오지 않아도 차속이 있으면 주행이다`() = runTest {
        val detector = startDetector()

        vehicle.speed(10.0)
        waitMs(5_000)

        assertEquals(DrivingState.Driving(startedAtMs = 0), detector.state.value)
    }

    @Test
    fun `P에 넣으면 주행 중이어도 바로 끝난다`() = runTest {
        val detector = startDriving()

        vehicle.stop()
        waitMs(60_000)
        vehicle.gear(Gear.PARK)
        waitMs(0)

        assertEquals(DrivingState.Idle, detector.state.value)
    }

    @Test
    fun `P가 아니면 오래 정차해도 주행을 이어간다`() = runTest {
        val detector = startDriving()

        vehicle.stop()
        waitMs(600_000)
        assertEquals(DrivingState.Driving(startedAtMs = 0), detector.state.value)

        vehicle.move()
        waitMs(10_000)
        assertEquals(DrivingState.Driving(startedAtMs = 0), detector.state.value)
    }

    @Test
    fun `P로 끝난 뒤 다시 출발하면 새 드라이빙이다`() = runTest {
        val detector = startDriving()

        // 5,000ms에 P, 15,000ms에 출발, 20,000ms에 주행
        vehicle.stop()
        vehicle.gear(Gear.PARK)
        waitMs(10_000)
        vehicle.move()
        waitMs(5_000)

        assertEquals(DrivingState.Driving(startedAtMs = 15_000), detector.state.value)
    }

    @Test
    fun `시동이 꺼지면 주행 중이어도 바로 끝난다`() = runTest {
        val detector = startDriving()

        vehicle.ignition(IgnitionState.OFF)
        waitMs(0)

        assertEquals(DrivingState.Idle, detector.state.value)
    }

    @Test
    fun `시동 ACC는 꺼짐으로 보지 않는다`() = runTest {
        val detector = startDriving()

        vehicle.ignition(IgnitionState.ACC)
        waitMs(10_000)

        assertEquals(DrivingState.Driving(startedAtMs = 0), detector.state.value)
    }

    // 0ms에 움직이기 시작해 5,000ms에 주행이 된 상태
    private fun TestScope.startDriving(): DrivingDetector {
        val detector = startDetector()
        vehicle.move()
        waitMs(5_000)
        assertEquals(DrivingState.Driving(startedAtMs = 0), detector.state.value)
        return detector
    }

    private fun TestScope.startDetector(): DrivingDetector {
        val detector = DrivingDetector(vehicle, { testScheduler.currentTime }, backgroundScope)
        detector.start()
        runCurrent()
        return detector
    }

    private fun TestScope.waitMs(ms: Long) {
        advanceTimeBy(ms)
        runCurrent()
    }
}

// 기어·차속·시동만 넣을 수 있는 가짜 저장소. 넣지 않은 신호는 값이 오지 않는 것과 같다.
private class FakeVehicleSignalRepository : VehicleSignalRepository {
    private val gearFlow = MutableSharedFlow<VehicleReading<Gear>>(replay = 1)
    private val speedFlow = MutableSharedFlow<VehicleReading<Double>>(replay = 1)
    private val ignitionFlow = MutableSharedFlow<VehicleReading<IgnitionState>>(replay = 1)

    override val gear: Flow<VehicleReading<Gear>> = gearFlow
    override val speedMps: Flow<VehicleReading<Double>> = speedFlow
    override val ignition: Flow<VehicleReading<IgnitionState>> = ignitionFlow
    override val lowBeamOn: Flow<VehicleReading<Boolean?>> = emptyFlow()
    override val highBeamOn: Flow<VehicleReading<Boolean?>> = emptyFlow()
    override val frontWipersOn: Flow<VehicleReading<Boolean?>> = emptyFlow()
    override val frontWheelAngleDeg: Flow<VehicleReading<Double?>> = emptyFlow()
    override val speedLimitKmh: Flow<VehicleReading<Double?>> = emptyFlow()

    override suspend fun readWheelbaseM(): Double? = null

    fun gear(value: Gear) {
        gearFlow.tryEmit(VehicleReading(0, value))
    }

    fun speed(value: Double) {
        speedFlow.tryEmit(VehicleReading(0, value))
    }

    fun ignition(value: IgnitionState) {
        ignitionFlow.tryEmit(VehicleReading(0, value))
    }

    fun move() {
        gear(Gear.DRIVE)
        speed(10.0)
    }

    fun stop() {
        speed(0.0)
    }
}
