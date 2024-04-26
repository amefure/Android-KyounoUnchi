package com.amefure.unchilog.View.TheDayDetail.RecycleViewSetting

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.amefure.unchilog.Model.Room.Poop
import com.amefure.unchilog.Model.Room.PoopColor
import com.amefure.unchilog.Model.Room.PoopShape
import com.amefure.unchilog.Model.Room.PoopVolume
import com.amefure.unchilog.R
import com.amefure.unchilog.Repository.SCCalender.shortSymbols
import com.amefure.unchilog.Utility.DateFormatUtility
import org.w3c.dom.Text
import java.time.DayOfWeek

class PoopRowAdapter(
    poopList: List<Poop>,
    private val context: Context
) : RecyclerView.Adapter<PoopRowAdapter.MainViewHolder>() {

    private val _poopList: MutableList<Poop> = poopList.toMutableList()
    override fun getItemCount(): Int = _poopList.size

    private val df = DateFormatUtility(format = "HH:mm")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(
            // XMLレイアウトファイルからViewオブジェクトを作成
            LayoutInflater.from(parent.context).inflate(R.layout.component_poop_row, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val poop = _poopList[position]

        // 時間
        holder.poopTime.text = df.getString(poop.createdAt)
        // 色
        val colorID = PoopColor.getColor(poop.color)
        holder.poopColor.backgroundTintList =
            ContextCompat.getColorStateList(context, colorID)
        // 硬い
        val imageId = PoopShape.getDrawable(poop.shape)
        val checkIcon: Drawable? = ContextCompat.getDrawable(context, imageId)
        holder.poopShape.setImageDrawable(checkIcon)
        // 量
        holder.poopVolume.text = PoopVolume.getName(poop.volume)
        // Memo
        holder.poopMemo.text = poop.memo
    }

    class MainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val poopTime: TextView = itemView.findViewById(R.id.poop_time)
        val poopColor: View = itemView.findViewById(R.id.poop_color)
        val poopShape: ImageView = itemView.findViewById(R.id.poop_shape)
        val poopVolume: TextView = itemView.findViewById(R.id.poop_volume)
        val poopMemo: TextView = itemView.findViewById(R.id.poop_memo)
    }
}