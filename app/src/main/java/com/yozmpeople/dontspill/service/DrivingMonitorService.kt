package com.yozmpeople.dontspill.service

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import com.yozmpeople.dontspill.R
import com.yozmpeople.dontspill.common.debugLog
import com.yozmpeople.dontspill.data.driving.DrivingRecorder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val TAG = "DrivingMonitorService"
private const val NOTIFICATION_ID = 1
private const val CHANNEL_ID = "driving_monitor"

// 주행 감지와 드라이빙 기록을 돌리는 location 타입 포그라운드 서비스. 시작·정지만 하고 기록은 DrivingRecorder가 한다.
// 포그라운드 서비스가 있으면 주행 중 시스템이 Activity를 닫아도 프로세스와 기록이 이어진다 (06 지시서)
@AndroidEntryPoint
class DrivingMonitorService : Service() {

    @Inject
    lateinit var drivingRecorder: DrivingRecorder

    // 바인드는 쓰지 않는다. startForegroundService로만 시작한다.
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        try {
            ServiceCompat.startForeground(
                this,
                NOTIFICATION_ID,
                buildNotification(),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
            )
        } catch (e: SecurityException) {
            // 위치 권한이 없거나, 화면 없이 다시 시작돼 "앱 사용 중에만 허용" 권한으로는 띄울 수 없을 때
            debugLog(TAG) { "포그라운드 시작 실패: $e" }
            stopSelf()
            return START_NOT_STICKY
        }
        drivingRecorder.start()
        return START_STICKY
    }

    // 주행 중이면 지금 시각으로 드라이빙을 끝낸다.
    override fun onDestroy() {
        drivingRecorder.stop()
        super.onDestroy()
    }

    // POST_NOTIFICATIONS는 요청하지 않는다. Pleos 에뮬레이터 알림 화면에 포그라운드 서비스 알림이 보이지 않았다 (06 지시서)
    private fun buildNotification(): Notification {
        val channel = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.driving_notification_channel),
            NotificationManager.IMPORTANCE_LOW
        )
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(getString(R.string.driving_notification_title))
            .setOngoing(true)
            .build()
    }

    companion object {
        // 화면이 떠 있을 때 부른다. 위치 권한은 "앱 사용 중에만 허용"이어도 된다 (추정, 문서 재확인 필요)
        fun startFromForeground(context: Context) {
            val granted = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
            if (!granted) {
                debugLog(TAG) { "위치 권한이 없어 서비스를 시작하지 않음" }
                return
            }
            ContextCompat.startForegroundService(context, Intent(context, DrivingMonitorService::class.java))
        }
    }
}
