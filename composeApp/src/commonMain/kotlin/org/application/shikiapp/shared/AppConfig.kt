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
        fun createDesktopConfig() = AppConfig(
            baseUrl = "https://shikimori.io",
            urlMirror = "https://shiki.one",
            userAgent = "ShikiApp",
            clientId = "C0IlIBQYqt9VHjuoayfbBG9ulhBH9XWuTOxSX_6oE6g",
            clientSecret = "0U2MtkFgtGUP9_TFKBw1ORVy6S68KZDz_AdKsoMfnFM",
            redirectUri = "app://login/"
        )
    }
}
