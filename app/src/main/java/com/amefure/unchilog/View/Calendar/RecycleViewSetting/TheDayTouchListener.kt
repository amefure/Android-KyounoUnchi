package com.amefure.unchilog.View.Calendar.RecycleViewSetting

import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.amefure.unchilog.Model.SCCalender.SCDate

class TheDayTouchListener: RecyclerView.SimpleOnItemTouchListener() {

    private var isScrollCount = 0
    private lateinit var listener: onTappedListener
    interface onTappedListener {
        fun onTapped(scdate: SCDate)
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
        // スクロール中は遷移させないようにする
        if (e.action == MotionEvent.ACTION_UP && isScrollCount <= 10) {
            // タッチされた位置のViewを取得
            val childView: View? = rv.findChildViewUnder(e.x, e.y)
            if (childView != null) {
                // 要素番号を取得
                val position = rv.getChildAdapterPosition(childView)
                if (position != RecyclerView.NO_POSITION) {
                    val adapter = rv.adapter
                    if (adapter is PoopCalendarAdapter) {
                        val tappedItem: SCDate? = adapter.getItemAtPosition(position)
                        if (tappedItem != null) {
                            listener.onTapped(tappedItem)
                        }
                    }
                }
            }
        } else if(e.action == MotionEvent.ACTION_MOVE) {
            isScrollCount += 1
        } else if(e.action == 1) {
            isScrollCount = 0
        }
        return false // 通常のタッチイベント処理を維持
    }

}