package com.amefure.unchilog.View.Calendar.RecycleViewSetting

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.amefure.unchilog.Model.Room.Poop
import com.amefure.unchilog.Model.SCCalender.SCDate
import com.amefure.unchilog.R
import com.amefure.unchilog.Repository.SCCalender.shortSymbols
import com.amefure.unchilog.Utility.DateFormatUtility
import java.util.Calendar
import java.util.Date

class PoopCalendarAdapter(
    dateList: List<SCDate>,
    private val poopList: List<Poop>,
    private val context: Context
    ) :RecyclerView.Adapter<PoopCalendarAdapter.MainViewHolder>() {

    private val _dateList: MutableList<SCDate> = dateList.toMutableList()
    override fun getItemCount(): Int = _dateList.size


    public fun getItemAtPosition(position: Int) : SCDate? {
        if (position < 0 || position >= _dateList.size) {
            return null
        }
        val item = _dateList[position]
        return item
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(
            // XMLレイアウトファイルからViewオブジェクトを作成
            LayoutInflater.from(parent.context).inflate(R.layout.component_calendar_the_day, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val scdate = _dateList[position]

        if (scdate.day != -1) {
            holder.day.text = scdate.day.toString()
            scdate.date?.let { date ->
                val count = poopList.count { DateFormatUtility.isSameDate(it.createdAt, date) }
                if (count != 0) {
                    holder.poopLayout.visibility = View.VISIBLE
                    holder.poopCnt.text = count.toString()
                }
                if (DateFormatUtility.isSameDate(Date(), date)) {
                    holder.day.backgroundTintList =
                        ContextCompat.getColorStateList(context, R.color.ex_sub)
                    holder.day.setTextColor(context.getColor(R.color.white))
                }
                val week = DateFormatUtility.getWeek(date)
                when (week) {
                    Calendar.SUNDAY -> holder.day.setTextColor(context.getColor(R.color.ex_week_holiday_sun))
                    Calendar.SATURDAY -> holder.day.setTextColor(context.getColor(R.color.ex_week_holiday_sat))
                    else -> {}
                }
            }
        } else {
            holder.day.text = ""
            holder.day.backgroundTintList =
                ContextCompat.getColorStateList(context, R.color.ex_gray)
            holder.theDayLayout.backgroundTintList =
                ContextCompat.getColorStateList(context, R.color.ex_gray)
        }
    }

    class MainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val theDayLayout: LinearLayout = itemView.findViewById(R.id.the_day_layout)
        val day: TextView = itemView.findViewById(R.id.day_label)
        val poopCnt: TextView = itemView.findViewById(R.id.poop_count)
        val poopLayout: ConstraintLayout = itemView.findViewById(R.id.poop_count_layout)
    }
}