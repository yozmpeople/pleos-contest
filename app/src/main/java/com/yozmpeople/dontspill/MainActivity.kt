package com.yozmpeople.dontspill

import ai.pleos.playground.vehicle.constant.PleosPermission
import android.Manifest
import android.car.Car
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.yozmpeople.dontspill.common.debugLog
import com.yozmpeople.dontspill.data.vehicle.VehicleSignalRepository
import com.yozmpeople.dontspill.service.DrivingMonitorService
import com.yozmpeople.dontspill.ui.DontSpillApp
import com.yozmpeople.dontspill.ui.character.CharacterRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch

private const val TAG = "VehicleSignals"

// FusedLocation SDK의 PleosPermission은 runtime 의존성(fused-location-data)에 있어 컴파일 때 쓸 수 없다.
private const val FUSED_LOCATION_PERMISSION = "pleos.car.permission.FUSED_LOCATION"

// 보호 수준을 모르는 권한도 함께 요청한다. normal 권한은 요청하면 바로 허용으로 온다.
// 위치는 Pleos FusedLocation으로 받는다. Android 위치는 주행 기록 서비스(location 타입)를 띄우는 데 필요하다.
private val REQUIRED_PERMISSIONS = arrayOf(
    Car.PERMISSION_SPEED,
    Car.PERMISSION_POWERTRAIN,
    Car.PERMISSION_CAR_INFO,
    PleosPermission.CAR_LIGHTS,
    PleosPermission.CAR_WINDOWS,
    PleosPermission.CAR_DRIVE,
    FUSED_LOCATION_PERMISSION,
    Manifest.permission.ACCESS_COARSE_LOCATION,
    Manifest.permission.ACCESS_FINE_LOCATION
)

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var vehicleSignalRepository: VehicleSignalRepository

    @Inject
    lateinit var characterRepository: CharacterRepository

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            debugLog(TAG) { "permissions $result" }
            onPermissionsResolved()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DontSpillApp(characterRepository)
        }

        // 임시: 에뮬레이터에서 차량 신호를 확인하는 연결이다.
        val granted = REQUIRED_PERMISSIONS.all { checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED }
        if (granted) onPermissionsResolved() else permissionLauncher.launch(REQUIRED_PERMISSIONS)
    }

    // 임시: 서비스 시작 지점(부팅 자동 / 캐릭터 선택)을 정한 뒤 바꾼다.
    private fun onPermissionsResolved() {
        logVehicleSignals()
        DrivingMonitorService.startFromForeground(this)
    }

    // 권한 요청 뒤에 구독한다. 권한 없이 먼저 등록하면 AAOS 신호는 등록되지 않은 채로 남는다.
    private fun logVehicleSignals() {
        val repository = vehicleSignalRepository
        lifecycleScope.launch {
            debugLog(TAG) { "wheelbaseM=${repository.readWheelbaseM()}" }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                merge(
                    repository.speedMps.map { "speedMps $it" },
                    repository.gear.map { "gear $it" },
                    repository.lowBeamOn.map { "lowBeamOn $it" },
                    repository.highBeamOn.map { "highBeamOn $it" },
                    repository.frontWipersOn.map { "frontWipersOn $it" },
                    repository.frontWheelAngleDeg.map { "frontWheelAngleDeg $it" },
                    repository.speedLimitKmh.map { "speedLimitKmh $it" }
                ).collect { message -> debugLog(TAG) { message } }
            }
        }
    }
}
