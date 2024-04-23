package com.amefure.unchilog.Repository.Room.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.amefure.unchilog.Model.Room.Poop
import io.reactivex.Flowable

@Dao
interface PoopDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertPoop(poop: Poop)

    @Update
    fun updatePoop(poop: Poop)

    @Query("SELECT * FROM poop_table")
    fun fetchAllPoops(): Flowable<List<Poop>>

    @Query("SELECT COUNT(*) FROM poop_table")
    fun getCount(): Int

    @Delete
    fun deletePoop(poop: Poop)
}