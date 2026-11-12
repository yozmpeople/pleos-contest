package com.yozmpeople.dontspill

import ai.pleos.playground.vehicle.constant.PleosPermission
import android.car.Car
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import com.yozmpeople.dontspill.common.debugLog
import com.yozmpeople.dontspill.service.DrivingMonitorService
import com.yozmpeople.dontspill.service.FUSED_LOCATION_PERMISSION
import com.yozmpeople.dontspill.ui.DontSpillApp
import com.yozmpeople.dontspill.ui.character.CharacterRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val TAG = "MainActivity"

// 보호 수준을 모르는 권한도 함께 요청한다. normal 권한은 요청하면 바로 허용으로 온다.
// 위치는 Pleos FusedLocation으로만 받는다.
private val REQUIRED_PERMISSIONS = arrayOf(
    Car.PERMISSION_SPEED,
    Car.PERMISSION_POWERTRAIN,
    Car.PERMISSION_CAR_INFO,
    PleosPermission.CAR_LIGHTS,
    PleosPermission.CAR_WINDOWS,
    PleosPermission.CAR_DRIVE,
    FUSED_LOCATION_PERMISSION
)

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

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

        val granted = REQUIRED_PERMISSIONS.all { checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED }
        if (granted) onPermissionsResolved() else permissionLauncher.launch(REQUIRED_PERMISSIONS)
    }

    // 권한 요청 뒤에 서비스를 시작한다. 권한 없이 먼저 등록하면 AAOS 신호는 등록되지 않은 채로 남는다.
    private fun onPermissionsResolved() {
        DrivingMonitorService.start(this)
    }
}
