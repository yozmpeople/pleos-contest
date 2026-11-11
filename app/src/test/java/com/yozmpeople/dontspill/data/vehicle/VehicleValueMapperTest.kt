package com.yozmpeople.dontspill.data.vehicle

import ai.pleos.playground.vehicle.constant.control.LightState
import ai.pleos.playground.vehicle.constant.control.WiperState
import android.car.VehicleGear
import android.car.VehicleIgnitionState
import com.yozmpeople.dontspill.data.vehicle.model.Gear
import com.yozmpeople.dontspill.data.vehicle.model.IgnitionState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class VehicleValueMapperTest {

    @Test
    fun `수동 단수는 전진이라 DRIVE로 본다`() {
        assertEquals(Gear.PARK, VehicleValueMapper.gear(VehicleGear.GEAR_PARK))
        assertEquals(Gear.REVERSE, VehicleValueMapper.gear(VehicleGear.GEAR_REVERSE))
        assertEquals(Gear.NEUTRAL, VehicleValueMapper.gear(VehicleGear.GEAR_NEUTRAL))
        assertEquals(Gear.DRIVE, VehicleValueMapper.gear(VehicleGear.GEAR_DRIVE))
        assertEquals(Gear.DRIVE, VehicleValueMapper.gear(VehicleGear.GEAR_FIRST))
        assertEquals(Gear.DRIVE, VehicleValueMapper.gear(VehicleGear.GEAR_NINTH))
    }

    @Test
    fun `모르는 기어 값은 UNKNOWN이다`() {
        assertEquals(Gear.UNKNOWN, VehicleValueMapper.gear(VehicleGear.GEAR_UNKNOWN))
        assertEquals(Gear.UNKNOWN, VehicleValueMapper.gear(3))
    }

    @Test
    fun `시동 상태는 AAOS 단계를 그대로 옮기고 모르는 값은 UNKNOWN이다`() {
        assertEquals(IgnitionState.LOCK, VehicleValueMapper.ignition(VehicleIgnitionState.LOCK))
        assertEquals(IgnitionState.OFF, VehicleValueMapper.ignition(VehicleIgnitionState.OFF))
        assertEquals(IgnitionState.ACC, VehicleValueMapper.ignition(VehicleIgnitionState.ACC))
        assertEquals(IgnitionState.ON, VehicleValueMapper.ignition(VehicleIgnitionState.ON))
        assertEquals(IgnitionState.START, VehicleValueMapper.ignition(VehicleIgnitionState.START))
        assertEquals(IgnitionState.UNKNOWN, VehicleValueMapper.ignition(VehicleIgnitionState.UNDEFINED))
    }

    @Test
    fun `조명은 ON과 OFF만 판단하고 주간주행등과 null은 모름이다`() {
        assertEquals(true, VehicleValueMapper.lightOn(LightState.ON))
        assertEquals(false, VehicleValueMapper.lightOn(LightState.OFF))
        @Suppress("DEPRECATION")
        assertNull(VehicleValueMapper.lightOn(LightState.DAYTIME_RUNNING))
        assertNull(VehicleValueMapper.lightOn(null))
    }

    @Test
    fun `와이퍼는 ON과 OFF만 판단하고 OTHER와 SERVICE와 null은 모름이다`() {
        assertEquals(true, VehicleValueMapper.wiperOn(WiperState.ON))
        assertEquals(false, VehicleValueMapper.wiperOn(WiperState.OFF))
        assertNull(VehicleValueMapper.wiperOn(WiperState.OTHER))
        assertNull(VehicleValueMapper.wiperOn(WiperState.SERVICE))
        assertNull(VehicleValueMapper.wiperOn(null))
    }

    @Test
    fun `차속과 전륜각은 부호를 유지한다`() {
        assertEquals(-2.5, VehicleValueMapper.speedMps(-2.5f), 0.0)
        assertEquals(-12.5, VehicleValueMapper.frontWheelAngleDeg(-12.5f)!!, 0.0)
        assertNull(VehicleValueMapper.frontWheelAngleDeg(null))
    }

    @Test
    fun `제한속도가 0 이하면 모름이다`() {
        assertEquals(60.0, VehicleValueMapper.speedLimitKmh(60)!!, 0.0)
        assertNull(VehicleValueMapper.speedLimitKmh(0))
        assertNull(VehicleValueMapper.speedLimitKmh(-1))
    }

    @Test
    fun `축거는 외형 치수 5번째 값을 m로 바꾼다`() {
        val dimensionsMm = intArrayOf(1_500, 4_500, 1_800, 2_000, 2_700, 1_550, 1_560, 11_000)

        assertEquals(2.7, VehicleValueMapper.wheelbaseM(dimensionsMm)!!, 1e-9)
    }

    @Test
    fun `축거 값이 없거나 0 이하면 모름이다`() {
        assertNull(VehicleValueMapper.wheelbaseM(null))
        assertNull(VehicleValueMapper.wheelbaseM(intArrayOf(1_500, 4_500, 1_800, 2_000)))
        assertNull(VehicleValueMapper.wheelbaseM(intArrayOf(1_500, 4_500, 1_800, 2_000, 0)))
    }
}
