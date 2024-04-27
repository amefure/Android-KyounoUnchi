package com.amefure.unchilog.View.Setting.RecycleViewSetting

import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class InitWeekTouchListener: RecyclerView.SimpleOnItemTouchListener() {

    private lateinit var listener: onTappedListener
    interface onTappedListener {
        fun onTapped(week: String)
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
                    if (adapter is InitWeekAdapter) {
                        val tappedItem: String = adapter.getItemAtPosition(position)
                        listener.onTapped(tappedItem)
                    }
                }
            }
        }
        return false
    }
}