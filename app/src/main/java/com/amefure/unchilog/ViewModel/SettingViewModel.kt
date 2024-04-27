package com.amefure.unchilog.ViewModel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.amefure.linkmark.Repository.DataStore.DataStoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class SettingViewModel (app: Application) : RootViewModel(app) {

    private val dataStoreRepository = DataStoreRepository(app.applicationContext)

    /**
     * アプリロックパスワード保存
     */
    public fun saveAppLockPassword(pass: Int) {
        viewModelScope.launch {
            dataStoreRepository.saveAppLockPassword(pass)
        }
    }

    /**
     * アプリロックパスワード観測
     */
    public fun observeAppLockPassword(): Flow<Int?> {
        return dataStoreRepository.observeAppLockPassword()
    }

    /**
     * 登録モード保存
     */
    public fun saveEntryMode(pass: Int) {
        viewModelScope.launch {
            dataStoreRepository.saveEntryMode(pass)
        }
    }

    /**
     * 登録モード観測
     */
    public fun observeEntryMode(): Flow<Int?> {
        return dataStoreRepository.observeEntryMode()
    }

    /**
     * 週始まり保存
     */
    public fun saveInitWeek(week: String) {
        viewModelScope.launch {
            dataStoreRepository.saveInitWeek(week)
        }
    }

    /**
     * 週始まり観測
     */
    public fun observeInitWeek(): Flow<String?> {
        return dataStoreRepository.observeInitWeek()
    }
}