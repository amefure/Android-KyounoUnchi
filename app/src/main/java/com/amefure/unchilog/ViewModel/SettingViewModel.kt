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
}