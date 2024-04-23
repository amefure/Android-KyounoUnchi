package com.amefure.unchilog.Repository.Room

import android.content.Context
import com.amefure.unchilog.Model.Poop
import com.amefure.unchilog.Repository.Room.Database.AppDatabase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import java.util.Date

class RootRepository (context: Context) {

    // Dao
    private val poopDao = AppDatabase.getDatabase(context).poopDao()

    // ゴミ箱
    private val compositeDisposable = CompositeDisposable()

    // Poop追加
    public fun insertPoop(color: String, shape: Int, volume: Int, memo: String, createdAt: Date) {
        val poop = Poop(
            id = 0,
            color = color,
            shape = shape,
            volume = volume,
            memo = memo,
            createdAt = createdAt
        )
        poopDao.insertPoop(poop)
    }

    // Poop更新
    public fun updatePoop(id: Int, color: String, shape: Int, volume: Int, memo: String, createdAt: Date) {
        val poop = Poop(
            id = id,
            color = color,
            shape = shape,
            volume = volume,
            memo = memo,
            createdAt = createdAt
        )
        poopDao.updatePoop(poop)
    }

    // Poop削除
    public fun deletePoop(poop: Poop) {
        poopDao.deletePoop(poop)
    }


    // Poop取得
    public fun fetchAllPoops(callback: (List<Poop>) -> Unit) {
        poopDao.fetchAllPoops()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { poops ->
                    callback(poops)
                }
            ).addTo(compositeDisposable)

    }



}