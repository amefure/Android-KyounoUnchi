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

        public fun isSameDate(date1: Date, date2: Date): Boolean {
            val cal1 = Calendar.getInstance()
            val cal2 = Calendar.getInstance()
            cal1.time = date1
            cal2.time = date2

            return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                    cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                    cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
        }
    }
}