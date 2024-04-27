package com.amefure.unchilog.View.TheDayDetail.RecycleViewSetting

import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.amefure.unchilog.Model.Room.Poop

class PoopRowTouchListener: RecyclerView.SimpleOnItemTouchListener() {

    private lateinit var listener: onTappedListener
    interface onTappedListener {
        fun onTapped(poop: Poop, rowView: View)
    }

    /**
     * リスナーのセットは使用するFragmentから呼び出して行う
     * リスナーオブジェクトの中に処理が含まれて渡される
     */
    public fun setOnTappedListener(listener: onTappedListener) {
        // 定義した変数listenerに実行したい処理を引数で渡す（CategoryListFragmentで渡している）
        this.listener = listener
    }

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        if (e.action == MotionEvent.ACTION_DOWN) {
            val childView: View? = rv.findChildViewUnder(e.x, e.y)
            if (childView != null) {
                val position = rv.getChildAdapterPosition(childView)
                if (position != RecyclerView.NO_POSITION) {
                    val adapter = rv.adapter
                    if (adapter is PoopRowAdapter) {
                        val tappedItem: Poop? = adapter.getItemAtPosition(position)
                        if (tappedItem != null) {
                            listener.onTapped(tappedItem, childView)
                        }
                    }
                }
            }
        }
        return false
    }
}