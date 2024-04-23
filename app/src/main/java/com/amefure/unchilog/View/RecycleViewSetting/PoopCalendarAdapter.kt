package com.amefure.unchilog.View.RecycleViewSetting

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.amefure.unchilog.Model.SCCalender.SCDate
import com.amefure.unchilog.R

class PoopCalendarAdapter(poopList: List<SCDate>) :RecyclerView.Adapter<PoopCalendarAdapter.MainViewHolder>() {

    private val _poopList: MutableList<SCDate> = poopList.toMutableList()
    override fun getItemCount(): Int = _poopList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(
            // XMLレイアウトファイルからViewオブジェクトを作成
            LayoutInflater.from(parent.context).inflate(R.layout.fragment_calendar_the_day, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val scdate = _poopList[position]

        if (scdate.day != -1) {
            holder.name.text = scdate.day.toString()
        }
    }

    class MainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.user_name)
        val hobby: TextView = itemView.findViewById(R.id.user_hobby)
    }
}