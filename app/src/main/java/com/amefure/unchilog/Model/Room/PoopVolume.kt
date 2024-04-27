package com.amefure.unchilog.Model.Room

enum class PoopVolume(val id: Int) {
    UNDEFINED(0) {
        override fun desc(): String = "未選択"
    },
    SMALL(1) {
        override fun desc(): String = "少なめ"
    },
    SEMISMALL(2) {
        override fun desc(): String = "やや少なめ"
    },
    MEDIUM(3) {
        override fun desc(): String = "中くらい"
    },
    SEMILLARGE(4) {
        override fun desc(): String = "やや多め"
    },
    LARGE(5) {
        override fun desc(): String = "多め"
    };
    abstract fun desc(): String

    companion object {
        fun getName(id: Int): String {
            return values().find { it.id == id }?.desc() ?: "中くらい"
        }
        fun getPoopVolume(id: Int): PoopVolume {
            return PoopVolume.values().find { it.id == id } ?: PoopVolume.UNDEFINED
        }
    }
}