package org.application.shikiapp.shared.utils

import org.application.shikiapp.shared.di.AppConfig

// =========================================== Strings ============================================


val BASE_URL = AppConfig.baseUrl
val URL_MIRROR = AppConfig.urlMirror
val API_URL = "$BASE_URL/api/"
val GRAPH_URL = "$BASE_URL/api/graphql"
val AUTH_URL = "$BASE_URL/oauth/authorize"
val TOKEN_URL = "$BASE_URL/oauth/token"

const val CODE = "code"
val CLIENT_ID = AppConfig.clientId
val CLIENT_SECRET = AppConfig.clientSecret
val REDIRECT_URI = AppConfig.redirectUri
const val GRANT_TYPE = "authorization_code"

const val PREF_GROUP_APP_VIEW = "app_view"
const val PREF_GROUP_APP_SYSTEM = "app_system"

const val PREF_APP_CACHE = "app_cache"
const val PREF_APP_LANGUAGE = "app_language"
const val PREF_APP_THEME = "app_theme"
const val PREF_CATALOG_LIST_VIEW = "catalog_list_view"
const val PREF_DYNAMIC_COLORS = "dynamic_colors"
const val PREF_START_PAGE = "start_page"

const val ACCESS_TOKEN = "access_token"
const val REFRESH_TOKEN = "refresh_token"
const val EXPIRES_IN = "expires_in"
const val CREATED_AT = "created_at"

const val USER_ID = "user_id"
const val BASE_PATH = "*"

const val BLANK = ""

// =========================================== Lists ==============================================
val CACHE_LIST = listOf(16, 32, 64, 128, 256, 512)

val ROLES_RUSSIAN = listOf("Автор оригинала", "Режиссёр", "Сюжет", "Сюжет и иллюстрации", "Рисовка")

// =========================================== Maps ===============================================
val EXTERNAL_LINK_KINDS = mapOf(
    "official_site" to "Официальный сайт",
    "wikipedia" to "Википедия",
    "anime_news_network" to "Anime News Network",
    "myanimelist" to "MyAnimeList",
    "anime_db" to "AniDB",
    "world_art" to "World Art",
    "kinopoisk" to "Кинопоиск",
    "kage_project" to "Kage Project",
    "twitter" to "Twitter/X",
    "smotret_anime" to "Anime 365",
    "shiki" to "Шикимори",
    "amediateka" to "Амедиатека",
    "crunchyroll" to "Crunchyroll",
    "amazon" to "Amazon",
    "hidive" to "Hidive",
    "hulu" to "Hulu",
    "ivi" to "ИВИ",
    "kinopoisk_hd" to "Кинопоиск HD",
    "wink" to "Wink",
    "netflix" to "Netflix",
    "okko" to "Okko",
    "youtube" to "Youtube",
    "readmanga" to "ReadManga",
    "mangalib" to "MangaLib",
    "remanga" to "ReManga",
    "mangaupdates" to "Baka-Updates",
    "mangadex" to "MangaDex",
    "mangafox" to "MangaFox",
    "mangachan" to "Mangachan",
    "mangahub" to "Mangahub",
    "novel_tl" to "Novel.tl",
    "ruranobe" to "RuRanobe",
    "ranobelib" to "RanobeLib",
    "novelupdates" to "Novel Updates"
)
