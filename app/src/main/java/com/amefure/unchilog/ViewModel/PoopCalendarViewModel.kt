package com.amefure.unchilog.ViewModel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.amefure.linkmark.Repository.DataStore.DataStoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class PoopCalendarViewModel(app: Application) : RootViewModel(app) {

    private val dataStoreRepository = DataStoreRepository(app.applicationContext)

    /**
     * インタースティシャルカウント保存
     */
    public fun saveInterstitialCount(count: Int) {
        viewModelScope.launch {
            dataStoreRepository.saveInterstitialCount(count)
        }
    }

    /**
     * インタースティシャルカウント観測
     */
    public fun observeInterstitialCount(): Flow<Int?> {
        return dataStoreRepository.observeInterstitialCount()
    }

    /**
     * 登録モード観測
     */
    public fun observeEntryMode(): Flow<Int?> {
        return dataStoreRepository.observeEntryMode()
    }

    /**
     * 週始まり観測
     */
    public fun observeInitWeek(): Flow<String?> {
        return dataStoreRepository.observeInitWeek()
    }
}
