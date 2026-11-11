package com.yozmpeople.dontspill.data.vehicle

import ai.pleos.playground.vehicle.constant.control.LightState
import ai.pleos.playground.vehicle.constant.control.WiperState
import android.car.VehicleGear
import com.yozmpeople.dontspill.data.vehicle.model.Gear

// INFO_EXTERIOR_DIMENSIONS 배열에서 축거 위치
private const val WHEELBASE_INDEX = 4
private const val MM_PER_M = 1000.0

// SDK 값을 앱 값으로 바꾼다. AAOS 상수는 컴파일 때 숫자로 들어가서 JVM 단위 테스트에서도 돈다.
object VehicleValueMapper {

    fun speedMps(value: Float): Double = value.toDouble()

    fun gear(value: Int): Gear = when (value) {
        VehicleGear.GEAR_PARK -> Gear.PARK
        VehicleGear.GEAR_REVERSE -> Gear.REVERSE
        VehicleGear.GEAR_NEUTRAL -> Gear.NEUTRAL
        VehicleGear.GEAR_DRIVE,
        VehicleGear.GEAR_FIRST,
        VehicleGear.GEAR_SECOND,
        VehicleGear.GEAR_THIRD,
        VehicleGear.GEAR_FOURTH,
        VehicleGear.GEAR_FIFTH,
        VehicleGear.GEAR_SIXTH,
        VehicleGear.GEAR_SEVENTH,
        VehicleGear.GEAR_EIGHTH,
        VehicleGear.GEAR_NINTH -> Gear.DRIVE
        else -> Gear.UNKNOWN
    }

    // ON만 켜짐, OFF만 꺼짐. 주간주행등(지원 안 함)과 null은 모름
    fun lightOn(state: LightState?): Boolean? = when (state) {
        LightState.ON -> true
        LightState.OFF -> false
        else -> null
    }

    // ON만 작동, OFF만 정지. OTHER·SERVICE와 null은 모름
    fun wiperOn(state: WiperState?): Boolean? = when (state) {
        WiperState.ON -> true
        WiperState.OFF -> false
        else -> null
    }

    fun frontWheelAngleDeg(value: Float?): Double? = value?.toDouble()

    // 0 이하는 제한 없음 또는 모름으로 본다 (추정, 에뮬레이터 확인 필요)
    fun speedLimitKmh(value: Int): Double? = value.takeIf { it > 0 }?.toDouble()

    // INFO_EXTERIOR_DIMENSIONS(mm)에서 축거를 m로 꺼낸다. 없거나 0 이하면 null
    fun wheelbaseM(dimensionsMm: IntArray?): Double? =
        dimensionsMm?.getOrNull(WHEELBASE_INDEX)?.takeIf { it > 0 }?.let { it / MM_PER_M }
}
