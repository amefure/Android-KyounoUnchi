package com.amefure.unchilog.ViewModel

import android.app.Application
import com.amefure.linkmark.Repository.DataStore.DataStoreRepository
import kotlinx.coroutines.flow.Flow

class TheDayDetailViewModel(app: Application) : RootViewModel(app) {

    private val dataStoreRepository = DataStoreRepository(app.applicationContext)

    /**
     * 登録モード観測
     */
    public fun observeEntryMode(): Flow<Int?> {
        return dataStoreRepository.observeEntryMode()
    }
}
