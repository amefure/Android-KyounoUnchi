package com.amefure.unchilog.View.Calendar.RecycleViewSetting

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.amefure.unchilog.Model.Room.Poop
import com.amefure.unchilog.Model.SCCalender.SCDate
import com.amefure.unchilog.R
import com.amefure.unchilog.Utility.DateFormatUtility
import java.util.Calendar
import java.util.Date

class PoopCalendarAdapter(
    dateList: List<SCDate>,
    private val poopList: List<Poop>
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
            }
        } else {
            holder.day.text = ""
        }
    }

    class MainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val day: TextView = itemView.findViewById(R.id.day_label)
        val poopCnt: TextView = itemView.findViewById(R.id.poop_count)
        val poopLayout: ConstraintLayout = itemView.findViewById(R.id.poop_count_layout)
    }


}