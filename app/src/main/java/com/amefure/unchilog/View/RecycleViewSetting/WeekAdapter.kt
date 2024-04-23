package com.amefure.unchilog.View.RecycleViewSetting

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.amefure.unchilog.R
import com.amefure.unchilog.Repository.SCCalender.shortSymbols
import java.time.DayOfWeek

@RequiresApi(Build.VERSION_CODES.O)
class WeekAdapter(weekList: List<DayOfWeek>) : RecyclerView.Adapter<WeekAdapter.MainViewHolder>() {

    private val _weekList: MutableList<DayOfWeek> = weekList.toMutableList()
    override fun getItemCount(): Int = _weekList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(
            // XMLレイアウトファイルからViewオブジェクトを作成
            LayoutInflater.from(parent.context).inflate(R.layout.fragment_calendar_the_day, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val week = _weekList[position]

        holder.name.text = week.shortSymbols
    }

    class MainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.user_name)
    }
}