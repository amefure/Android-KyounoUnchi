package com.amefure.unchilog.ViewModel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.amefure.linkmark.Repository.DataStore.DataStoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class PoopCalendarViewModel(app: Application) : RootViewModel(app) {

    private val dataStoreRepository = DataStoreRepository(app.applicationContext)

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
