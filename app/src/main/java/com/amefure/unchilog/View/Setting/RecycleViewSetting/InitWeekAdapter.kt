package com.amefure.unchilog.View.Setting.RecycleViewSetting

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.amefure.unchilog.Model.Room.Poop
import com.amefure.unchilog.R

class InitWeekAdapter(
    private val selectWeek: String
    ) : RecyclerView.Adapter<InitWeekAdapter.MainViewHolder>() {

    private val weekList: List<String> = listOf("日曜日", "月曜日", "火曜日", "水曜日", "木曜日", "金曜日", "土曜日")
    override fun getItemCount(): Int = weekList.size

    public fun getItemAtPosition(position: Int) : String {
        if (position < 0 || position >= weekList.size) {
            return "日曜日"
        }
        val item = weekList[position]
        return item
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(
            // XMLレイアウトファイルからViewオブジェクトを作成
            LayoutInflater.from(parent.context).inflate(R.layout.component_init_week, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val week = weekList[position]
        holder.weekText.setText(week)
        if (week == selectWeek) {
            holder.checkMark.visibility = View.VISIBLE
        }
    }
    class MainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val weekText: TextView = itemView.findViewById(R.id.week_label)
        val checkMark: ImageView = itemView.findViewById(R.id.check_mark)
    }
}