package com.amefure.unchilog.Model.Room

enum class PoopShape(val id: Int) {
    UNDEFINED(0) {
        override fun desc(): String = "未選択"
    },
    KOROKORO(1) {
        override fun desc(): String = "硬め"
    },
    SEMIKOROKORO(2) {
        override fun desc(): String = "やや硬め"
    },
    NORMAL(3) {
        override fun desc(): String = "中くらい"
    },
    SEMILIQUID(4) {
        override fun desc(): String = "やや柔らかめ"
    },
    LIQUID(5) {
        override fun desc(): String = "柔らかめ"
    };
    abstract fun desc(): String
}