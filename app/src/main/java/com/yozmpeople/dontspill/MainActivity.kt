package com.yozmpeople.dontspill

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 화면은 03(화면 기반)·04(화면 UI)에서 만든다.
        setContent {
            Text(text = getString(R.string.app_name))
        }
    }
}
