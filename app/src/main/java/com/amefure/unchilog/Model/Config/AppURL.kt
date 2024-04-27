package com.amefure.unchilog.Model.Config

class AppURL {
    companion object {
        // WebViewで有効でないURLだった場合の代替用URL
        public const val SUBSTITUTE_URL = "https://appdev-room.com/"
        // お問い合わせ
        public const val INQUIRY_URL = SUBSTITUTE_URL + "contact"
        // 利用規約&プライバシーポリシー
        public const val TERMS_OF_SERVICE_URL = SUBSTITUTE_URL + "app-terms-of-service"
    }
}