package com.yozmpeople.dontspill.common

import android.util.Log
import com.yozmpeople.dontspill.BuildConfig

// debug 빌드용 로그
inline fun debugLog(tag: String, message: () -> String) {
    if (BuildConfig.DEBUG) Log.d("Chaduck$tag", message())
}
