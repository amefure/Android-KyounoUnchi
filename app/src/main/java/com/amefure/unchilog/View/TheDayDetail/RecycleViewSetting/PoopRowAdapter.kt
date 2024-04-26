package com.amefure.unchilog.View.TheDayDetail.RecycleViewSetting

import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.amefure.unchilog.Model.Room.Poop
import com.amefure.unchilog.Model.Room.PoopColor
import com.amefure.unchilog.Model.Room.PoopShape
import com.amefure.unchilog.Model.Room.PoopVolume
import com.amefure.unchilog.R
import com.amefure.unchilog.Repository.SCCalender.shortSymbols
import java.time.DayOfWeek

@RequiresApi(Build.VERSION_CODES.O)
class PoopRowAdapter(poopList: List<Poop>) : RecyclerView.Adapter<PoopRowAdapter.MainViewHolder>() {

    private val _poopList: MutableList<Poop> = poopList.toMutableList()
    override fun getItemCount(): Int = _poopList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(
            // XMLレイアウトファイルからViewオブジェクトを作成
            LayoutInflater.from(parent.context).inflate(R.layout.component_poop_row, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val poop = _poopList[position]

        val colorID = PoopColor.getColor(poop.color)
        holder.poopColor.setBackgroundColor(colorID)
        val imageId = PoopShape.getDrawable(poop.shape)

        val checkIcon: Drawable? = ResourcesCompat.getDrawable(Resources.getSystem(), imageId, null)
        holder.poopShape.setImageDrawable(checkIcon)
        holder.poopVolume.text = PoopVolume.getName(poop.volume)
        holder.poopMemo.text = poop.memo
    }

    class MainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val poopColor: View = itemView.findViewById(R.id.poop_color)
        val poopShape: ImageView = itemView.findViewById(R.id.poop_shape)
        val poopVolume: TextView = itemView.findViewById(R.id.poop_volume)
        val poopMemo: TextView = itemView.findViewById(R.id.poop_memo)
    }
}