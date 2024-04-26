package com.amefure.unchilog.Model.Room

import com.amefure.unchilog.R

enum class PoopShape(val id: Int) {
    UNDEFINED(0) {
        override fun desc(): String = "未選択"
        override fun drawable(): Int = R.drawable.noface_poop
    },
    KOROKORO(1) {
        override fun desc(): String = "硬め"
        override fun drawable(): Int = R.drawable.poop_shape1
    },
    SEMIKOROKORO(2) {
        override fun desc(): String = "やや硬め"
        override fun drawable(): Int = R.drawable.poop_shape2
    },
    NORMAL(3) {
        override fun desc(): String = "中くらい"
        override fun drawable(): Int = R.drawable.poop_shape3
    },
    SEMILIQUID(4) {
        override fun desc(): String = "やや柔らかめ"
        override fun drawable(): Int = R.drawable.poop_shape4
    },
    LIQUID(5) {
        override fun desc(): String = "柔らかめ"
        override fun drawable(): Int = R.drawable.poop_shape5
    };
    abstract fun desc(): String
    abstract fun drawable(): Int


    companion object {
        fun getName(id: Int): String {
            return PoopShape.values().find { it.id == id }?.desc() ?: "中くらい"
        }
        fun getDrawable(id: Int): Int {
            return PoopShape.values().find { it.id == id }?.drawable() ?: R.drawable.noface_poop
        }
    }
}