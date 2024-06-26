package com.amefure.unchilog.ViewModel

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.amefure.unchilog.Model.Room.Poop
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

class PoopViewModel (app: Application) : RootViewModel(app) {

    private val _poops = MutableLiveData<List<Poop>>()
    public val poops: LiveData<List<Poop>> = _poops

    /**
     * Poops取得
     * MutableLiveDataに格納する
     */
    public fun fetchAllPoops() {
        // データの取得は非同期で
        viewModelScope.launch(Dispatchers.IO) {  // データ取得はIOスレッドで
            rootRepository.fetchAllPoops {
                _poops.postValue(it.sortedBy { it.createdAt })  // 本来はDBやCacheから取得
            }
        }
    }

    /**
     * Poop取得
     * MutableLiveDataに格納する
     */
    public fun fetchSinglePoop(id: Int, callback: (Poop) -> Unit ) {
        // データの取得は非同期で
        viewModelScope.launch(Dispatchers.IO) {  // データ取得はIOスレッドで
            rootRepository.fetchSinglePoop(id) {
                callback(it)
            }
        }
    }

    /**
     * 追加
     */
    public fun insertPoop(color: Int = 0, shape: Int = 0, volume: Int = 0, memo: String = "", createdAt: Date) {
        viewModelScope.launch(Dispatchers.IO) {
            rootRepository.insertPoop(color, shape, volume, memo, createdAt)
            fetchAllPoops()
        }
    }


    /**
     * 更新
     */
    public fun updatePoop(id: Int, color: Int, shape: Int, volume: Int, memo: String, createdAt: Date) {
        viewModelScope.launch(Dispatchers.IO) {
            rootRepository.updatePoop(id, color, shape, volume, memo, createdAt)
        }
    }

    /**
     * 削除
     */
    public fun deletePoop(poop: Poop) {
        viewModelScope.launch(Dispatchers.IO) {
            rootRepository.deletePoop(poop)
        }
    }
}