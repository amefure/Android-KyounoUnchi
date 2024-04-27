package com.amefure.unchilog.View

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import com.amefure.linkmark.Repository.DataStore.DataStoreRepository
import com.amefure.unchilog.R
import com.amefure.unchilog.View.AppLock.AppLockFragment
import com.amefure.unchilog.View.Calendar.PoopCalendarFragment
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : AppCompatActivity() {

    private val dataStoreRepository = DataStoreRepository(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // 初期化
        MobileAds.initialize(this)

        lifecycleScope.launch {
            var result = dataStoreRepository.observeAppLockPassword().first()
            if (result == null || result == 0) {
                supportFragmentManager.beginTransaction().apply {
                    add(R.id.main_frame, PoopCalendarFragment())
                    addToBackStack(null)
                    commit()
                }
            } else {
                supportFragmentManager.beginTransaction().apply {
                    add(R.id.main_frame, AppLockFragment())
                    addToBackStack(null)
                    commit()
                }
            }
        }
    }
}