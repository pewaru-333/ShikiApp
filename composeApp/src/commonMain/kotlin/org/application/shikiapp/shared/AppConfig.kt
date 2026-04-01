package org.application.shikiapp.shared

data class AppConfig(
    val baseUrl: String,
    val urlMirror: String,
    val userAgent: String,
    val clientId: String,
    val clientSecret: String,
    val redirectUri: String,
    val isShikimori: Boolean = userAgent == "ShikiApp",
    val authScopes: List<String> = if (isShikimori) listOf("user_rates", /*"email"*/ "messages", "comments", "topics", /*"content"*/ "clubs", "friends" /*"ignores"*/)
    else listOf("user_rates", /*"email"*/ "messages", "comments", "topics", /*"content"*/ "clubs", "friends", "ignores")
) {
    companion object {
        private val ShikiApp = AppConfig(
            baseUrl = "https://shikimori.io",
            urlMirror = "https://shiki.one",
            userAgent = "ShikiApp",
            clientId = "C0IlIBQYqt9VHjuoayfbBG9ulhBH9XWuTOxSX_6oE6g",
            clientSecret = "0U2MtkFgtGUP9_TFKBw1ORVy6S68KZDz_AdKsoMfnFM",
            redirectUri = "app://login"
        )
        private val DarkShiki = AppConfig(
            baseUrl = "https://shikimori.fi",
            urlMirror = "https://shikimori.rip",
            userAgent = "DarkShiki",
            clientId = "d8W9rjFLuEZKx_dYXzJ42nGvsUckx4vfhMu5Liyr7MY",
            clientSecret = "Sog_CyJs19eCuFbIvg06Gb8zu8AMXZE8VI2CKLN1td4",
            redirectUri = "darkshiki://auth/login"
        )

        fun createFlavourConfig(userAgent: String) = if (userAgent == "ShikiApp") ShikiApp
        else DarkShiki

        fun createDesktopConfig() = ShikiApp.copy(
            redirectUri = "app://login/"
        )
    }
}