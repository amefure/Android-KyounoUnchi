package com.amefure.unchilog.ViewModel

import android.app.Application
import com.amefure.unchilog.Repository.Room.RootRepository

/**
 *   マニフェストファイルに
 *   [android:name=".ViewModel.RootApplication"]を追加
 */
class RootApplication : Application() {
    /**
     * [RootRepository]のインスタンス
     */
    val rootRepository: RootRepository by lazy { RootRepository(this) }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onTerminate() {
        super.onTerminate()
    }

}