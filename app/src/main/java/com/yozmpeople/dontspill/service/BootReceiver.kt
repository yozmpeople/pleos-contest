package com.yozmpeople.dontspill.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

// 차가 켜지면 앱을 열지 않아도 주행 감지를 시작한다. 캐릭터는 기본값(물컵이)으로 기록한다.
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        DrivingMonitorService.start(context)
    }
}
