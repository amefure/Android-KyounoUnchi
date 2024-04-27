package com.amefure.unchilog.Model.Room

import com.amefure.unchilog.R

enum class PoopColor(var id: Int) {

    UNDEFINED(0) {
        override fun color(): Int = R.color.ex_poop_black
    },
    // 黄褐色・・・正常な便の色調です。これは胆汁色素ビリルビンによると考えられています。
    YELLOWISHBROWN(1) {
        override fun color(): Int = R.color.ex_poop_yellowish_brown
    },
    // 黄色・・・高度の下痢便などで見られます。牛乳の多飲、下剤の服用や脂肪便の時でも見られます。
    YELLOW(2) {
        override fun color(): Int = R.color.ex_poop_yellow
    },
    // 茶～茶褐色・・・食べ過ぎ、飲み過ぎの場合。
    BROWN(3) {
        override fun color(): Int = R.color.ex_poop_brown
    },
    // 濃褐色・・・便秘の時や肉類の多い食事で見られます。また、ココアやチョコレートを大量に食べる人でもこの様な色になります。
    DARKBROWN(4) {
        override fun color(): Int = R.color.ex_poop_dark_brown
    },
    // 黒色・・・上部消化管の出血でコールタールに似ているため、タール便ともいいます。また、イカ墨料理を食べた後、鉄剤の服用後、また薬用炭も黒色便になります。
    BLACK(5) {
        override fun color(): Int = R.color.ex_poop_black
    },
    // 緑色・・・母乳の赤ちゃんの便や緑色を呈したクロロフィルを多く含む緑色野菜を大量に食べる人の便は緑色調になります。
    GREEN(6) {
        override fun color(): Int = R.color.ex_poop_green
    },
    // 赤色・・・赤色は出血した場所が肛門に近いほど新鮮血を呈します。痔核や肛門裂傷は血液そのものの新鮮血で、Ｓ状結腸や直腸からの出血は、新鮮血と凝血した血液の固まりを含んでいます。上行結腸や横行結腸からの出血は濃紫色、血液が便の周囲に付着しているのは直腸や肛門からの出血と考えられます。直ちに、専門医療機関への受診が必要です。
    RED(7) {
        override fun color(): Int = R.color.ex_poop_red
    },
    // 灰白色・・・胆汁の流出が悪いか、胃透視時のバリウムによるものです。腸結核や膵疾患でもこの様な色になることがあります。
    GRAYISHWHITE(8) {
        override fun color(): Int = R.color.ex_poop_grayish_white
    };

    abstract fun color(): Int

    companion object {
        fun getColor(id: Int): Int {
            return PoopColor.values().find { it.id == id }?.color() ?: R.color.ex_poop_black
        }
        fun getPoopColor(id: Int): PoopColor {
            return PoopColor.values().find { it.id == id } ?: UNDEFINED
        }
    }

}