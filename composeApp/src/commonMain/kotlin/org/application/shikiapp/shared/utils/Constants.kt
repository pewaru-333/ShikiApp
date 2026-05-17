package org.application.shikiapp.shared.utils

// =========================================== Strings ============================================

const val PREF_APP_CACHE = "app_cache"
const val PREF_APP_LANGUAGE = "app_language"
const val PREF_APP_THEME = "app_theme"
const val PREF_CATALOG_LIST_VIEW = "catalog_list_view"
const val PREF_COLOR_PALETTE = "app_colors_palette"
const val PREF_DEEP_LINK_SETTINGS = "deeplink_settings"
const val PREF_DYNAMIC_COLORS = "dynamic_colors"
const val PREF_EPISODE_AUTO_ADD = "auto_increment_episode"
const val PREF_GROUP_APP_LISTS = "app_lists_behaviour"
const val PREF_GROUP_APP_SYSTEM = "app_system"
const val PREF_GROUP_APP_VIEW = "app_view"
const val PREF_HAS_AGREED_TO_WATCH = "agreed_to_watch"
const val PREF_SHOW_USER_RATES_LIST_TAB_SIZE = "show_user_rates_list_tab_size"
const val PREF_START_PAGE = "start_page"
const val PREF_USER_RATES_START_TYPE = "user_rates_start_type"
const val PREF_USER_RATES_START_WATCH_STATUS = "user_rates_start_watch_status"

const val ACCESS_TOKEN = "access_token"
const val REFRESH_TOKEN = "refresh_token"
const val EXPIRES_IN = "expires_in"
const val CREATED_AT = "created_at"

const val USER_ID = "user_id"

const val BLANK = ""

// ======================================== Collections ============================================
val CACHE_LIST = listOf(16, 32, 64, 128, 256, 512)
val ROLES_RUSSIAN = setOf("Автор оригинала", "Режиссёр", "Сюжет", "Сюжет и иллюстрации", "Рисовка")

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