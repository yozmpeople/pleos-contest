package com.yozmpeople.dontspill

import ai.pleos.playground.vehicle.constant.PleosPermission
import android.car.Car
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Text
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.yozmpeople.dontspill.common.debugLog
import com.yozmpeople.dontspill.data.vehicle.VehicleSignalRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch

private const val TAG = "VehicleSignals"

// 보호 수준을 모르는 권한도 함께 요청한다. normal 권한은 요청하면 바로 허용으로 온다.
private val VEHICLE_PERMISSIONS = arrayOf(
    Car.PERMISSION_SPEED,
    Car.PERMISSION_POWERTRAIN,
    Car.PERMISSION_CAR_INFO,
    PleosPermission.CAR_LIGHTS,
    PleosPermission.CAR_WINDOWS,
    PleosPermission.CAR_DRIVE
)

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var vehicleSignalRepository: VehicleSignalRepository

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            debugLog(TAG) { "permissions $result" }
            logVehicleSignals()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 화면은 03(화면 기반)·04(화면 UI)에서 만든다.
        setContent {
            Text(text = getString(R.string.app_name))
        }

        // 임시: 에뮬레이터에서 차량 신호를 확인하는 연결이다. 화면 지시서에서 대체한다.
        val granted = VEHICLE_PERMISSIONS.all { checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED }
        if (granted) logVehicleSignals() else permissionLauncher.launch(VEHICLE_PERMISSIONS)
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
                    repository.ignition.map { "ignition $it" },
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
