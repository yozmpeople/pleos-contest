package com.yozmpeople.dontspill

import ai.pleos.playground.vehicle.constant.PleosPermission
import android.Manifest
import android.car.Car
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import com.yozmpeople.dontspill.common.debugLog
import com.yozmpeople.dontspill.service.DrivingMonitorService
import com.yozmpeople.dontspill.ui.DontSpillApp
import com.yozmpeople.dontspill.ui.character.CharacterRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val TAG = "MainActivity"

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
    lateinit var characterRepository: CharacterRepository

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            debugLog(TAG) { "permissions $result" }
            onPermissionsResolved()
        }

    // 앱 사용 중 위치를 허용한 뒤에만 요청할 수 있다. 시스템 설정 화면에서 "항상 허용"을 고른다.
    private val backgroundLocationLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            debugLog(TAG) { "background location $granted" }
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
        DrivingMonitorService.startFromForeground(this)
        requestBackgroundLocation()
    }

    // 부팅 뒤 앱을 열지 않아도 주행을 기록하려면 "항상 허용"이 필요하다.
    private fun requestBackgroundLocation() {
        val foregroundGranted =
            checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val backgroundGranted =
            checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (foregroundGranted && !backgroundGranted) {
            backgroundLocationLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }
    }
}
