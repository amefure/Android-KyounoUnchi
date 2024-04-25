package com.amefure.unchilog.Model.Room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.util.Date

@Entity(tableName = "poop_table")
data class Poop(
    @PrimaryKey(autoGenerate = true) val id: Int,
    var color: Int,
    var shape: Int,
    var volume: Int,
    var memo: String,
    @ColumnInfo(name = "created_at")
    val createdAt: Date
)

public class DateConverters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?) : Long? {
        return date?.time
    }
}