package com.amefure.unchilog.Model.SCCalender

import java.time.DayOfWeek
import java.util.Date

data class SCDate(

    var year: Int,
    var month: Int,
    var day: Int,
    var date: Date? = null,
    var week: DayOfWeek? = null,
    var holidayName: String = ""

) {

}