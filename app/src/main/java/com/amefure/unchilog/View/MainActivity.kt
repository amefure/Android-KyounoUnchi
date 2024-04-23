package com.amefure.unchilog.View

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.amefure.unchilog.R
import com.google.android.gms.ads.MobileAds

@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // 初期化
        MobileAds.initialize(this)

        supportFragmentManager.beginTransaction().apply {
            add(R.id.main_frame, PoopCalendarFragment())
            addToBackStack(null)
            commit()
        }
    }
}