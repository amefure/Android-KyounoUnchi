package com.amefure.unchilog.Repository.SCCalender

import android.os.Build
import androidx.annotation.RequiresApi
import com.amefure.unchilog.Model.SCCalender.SCDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import kotlin.math.abs


// java.timeを使用するためにビルドバージョン保証
@RequiresApi(Build.VERSION_CODES.O)
class SCCalenderRepository {

    companion object {
        private val START_YEAR = 2023
        private val START_MONTH = 1
    }

    /// 最初に表示したい曜日
    private var initWeek: DayOfWeek = DayOfWeek.SUNDAY

    // 表示している年月の日付オブジェクト
    private val _currentDates = MutableStateFlow<List<SCDate>>(emptyList())
    val currentDates: Flow<List<SCDate>> = _currentDates

    // 表示している年月オブジェクト
    private val _currentYearAndMonth = MutableStateFlow<YearMonth?>(null)
    val currentYearAndMonth: Flow<YearMonth?> = _currentYearAndMonth

    // 表示している曜日配列(順番はUIに反映される)
    private val _dayOfWeekList = MutableStateFlow(listOf(DayOfWeek.SUNDAY, DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY))
    val dayOfWeekList: Flow<List<DayOfWeek>> = _dayOfWeekList

    /// 表示可能な年月配列
    public var selectYearAndMonth: List<YearMonth> = emptyList()

    /// 表示している年月オブジェクトIndex
    private var currentYearAndMonthIndex: Int = 0

    constructor(initWeek: DayOfWeek = DayOfWeek.SUNDAY) {
        create(initWeek)
    }

    private fun create(initWeek: DayOfWeek) {
        this.initWeek = initWeek

        val today = Calendar.getInstance()
        val nowYear = today.get(Calendar.YEAR)
        val nowMonth = today.get(Calendar.MONTH) + 1

        // 表示可能な年月情報を生成し保持
        setSelectYearAndMonth(startYear = START_YEAR, endYear = nowYear)
        // カレンダーの初期表示位置
        moveYearAndMonthCalendar(year = nowYear, month = nowMonth)
        // 週の始まりに設定する曜日を指定
        setFirstWeek(initWeek)
    }

    /**
     * 指定した年月の範囲の`SCYearAndMonth`オブジェクトを生成
     * - parameter startYear: 開始年月
     * - parameter endYear: 終了年月
     */
    private fun setSelectYearAndMonth(startYear: Int, endYear: Int) {
        var yearMonthList: MutableList<YearMonth> = mutableListOf()
        // 当年+1年先のカレンダー情報を取得しておく
        for (year in startYear..endYear + 1) {
            for (month in 1..12) {
                val yearMonth = YearMonth.of(year, month)
                yearMonthList.add(yearMonth)
            }
        }
        selectYearAndMonth = yearMonthList
    }
    /// カレンダーUIを更新
    /// 日付情報を取得して配列に格納
    private fun updateCalendar() {
        val yearMonth = _currentYearAndMonth.value ?:return
        val year = yearMonth.year
        val month = yearMonth.monthValue

        // 指定された年月の日数を取得
        val range = yearMonth.lengthOfMonth()

        // 日にち情報を格納する配列を準備
        var dates: MutableList<SCDate> = mutableListOf()

        // 月の初めから最後の日までループして日にち情報を作成
        for (day in 1..range) {

            val localDate = LocalDate.of(year, month, day)
            val date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
            val week = localDate.dayOfWeek

            val holidayName = "" // ここに祝日名を取得する処理を追加する
            val scDate = SCDate(year = year, month = month, day = day, date = date, week = week, holidayName = holidayName)
            dates.add(scDate)
        }

        val firstWeek = _dayOfWeekList.value.indexOfFirst { it == dates.first()!!.week }
        val initWeek = _dayOfWeekList.value.indexOfFirst { it == initWeek }
        val sabun = abs(firstWeek - initWeek)


        if (sabun != 0) {
            for (i in 0 until sabun) {
                val blankScDate = SCDate(year = -1, month = -1, day = -1)
                dates.add(0, blankScDate)
            }
        }

        if (dates.size % 7 != 0) {
            val space = 7 - dates.size % 7
            for (i in 0 until space) {
                val blankScDate = SCDate(year = -1, month = -1, day = -1)
                dates.add(blankScDate)
            }
        }
        _currentDates.value = dates
    }

    /// 年月を1つ進める
    public fun forwardMonth() {
        currentYearAndMonthIndex += 1
        val nextYearAndMonth = selectYearAndMonth[currentYearAndMonthIndex]
        _currentYearAndMonth.value = nextYearAndMonth
        updateCalendar()
    }

    /// 年月を1つ戻す
    public fun backMonth() {
        currentYearAndMonthIndex -= 1
        val nextYearAndMonth = selectYearAndMonth[currentYearAndMonthIndex]
        _currentYearAndMonth.value = nextYearAndMonth
        updateCalendar()
    }

    /// 最初に表示したい曜日を設定
    /// - parameter week: 開始曜日
    public fun setFirstWeek(week: DayOfWeek) {
        initWeek = week
        var list = _dayOfWeekList.value.toMutableList()
        list.moveWeekToFront(initWeek)
        _dayOfWeekList.value = list
        updateCalendar()
    }

    /// カレンダー初期表示年月を指定して更新
    /// - parameter year: 指定年
    /// - parameter month: 指定月
    public fun moveYearAndMonthCalendar(year: Int, month: Int) {
        currentYearAndMonthIndex = selectYearAndMonth.indexOfFirst { it.year == year && it.monthValue == month }
        _currentYearAndMonth.value = selectYearAndMonth.get(currentYearAndMonthIndex)
        updateCalendar()
    }
}


fun MutableList<DayOfWeek>.moveWeekToFront(week: DayOfWeek) {
    val index = indexOf(week)
    if (index != -1) {
        val newList = subList(index, size).toMutableList() + subList(0, index)
        clear()
        addAll(newList)
    }
}
