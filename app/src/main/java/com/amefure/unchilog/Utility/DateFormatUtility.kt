package com.amefure.unchilog.Utility

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class DateFormatUtility(format: String = "yyyy年MM月dd日") {

    private val df = SimpleDateFormat(format)

    public fun getString(date: Date): String {
        return df.format(date)
    }

    companion object {
        public fun getWeek(date: Date): Int {
            // 曜日を取得するためにCalendarクラスを使用
            val calendar = Calendar.getInstance()
            calendar.time = date
            return calendar.get(Calendar.DAY_OF_WEEK)
        }

        public fun isSameMonthDate(date1: Date, date2: Date): Boolean {
            val cal1 = Calendar.getInstance()
            val cal2 = Calendar.getInstance()
            cal1.time = date1
            cal2.time = date2

            return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                    cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
        }

        public fun isSameDate(date1: Date, date2: Date): Boolean {
            val cal1 = Calendar.getInstance()
            val cal2 = Calendar.getInstance()
            cal1.time = date1
            cal2.time = date2

            return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                    cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                    cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
        }

        public fun getSetTimeDate(date: Date, hour: Int, minute: Int): Date {
            var now = date
            val calendar = Calendar.getInstance()
            calendar.time = now
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            val updatedDate = calendar.time
            return updatedDate
        }
    }
}