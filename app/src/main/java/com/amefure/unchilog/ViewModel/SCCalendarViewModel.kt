package com.amefure.unchilog.ViewModel

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import com.amefure.unchilog.Model.SCCalender.SCDate
import com.amefure.unchilog.Repository.SCCalender.SCCalenderRepository
import java.time.YearMonth
import kotlinx.coroutines.flow.Flow
import java.time.DayOfWeek

@RequiresApi(Build.VERSION_CODES.O)
class SCCalendarViewModel(app: Application) : RootViewModel(app) {

    // カレンダーロジックリポジトリ

    private var sccalenderRepository = SCCalenderRepository.shared

    public val currentDates: Flow<List<SCDate>> = sccalenderRepository.currentDates
    public val currentYearAndMonth: Flow<YearMonth?> = sccalenderRepository.currentYearAndMonth
    public val dayOfWeekList: Flow<List<DayOfWeek>> = sccalenderRepository.dayOfWeekList

    public fun updateInitWeek(dayOfWeek: DayOfWeek) {
        sccalenderRepository.updateInitWeek(dayOfWeek)
    }

    public fun forwardMonth(): Boolean {
        return sccalenderRepository.forwardMonth()
    }
    public fun backMonth(): Boolean {
        return sccalenderRepository.backMonth()
    }

    public fun moveYearAndMonthCalendar(year: Int, month: Int) {
        sccalenderRepository.moveYearAndMonthCalendar(year, month)
    }
}
