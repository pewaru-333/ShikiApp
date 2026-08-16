package org.application.shikiapp.shared.utils

import shikiapp.composeapp.generated.resources.*

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
const val PREF_LAST_SORTING_ORDER = "catalog_list_last_sort_order"
const val PREF_REMEMBER_CATALOG_LAST_ORDER = "remember_catalog_list_last_sort_order"
const val PREF_SHOW_USER_RATES_LIST_TAB_SIZE = "show_user_rates_list_tab_size"
const val PREF_START_PAGE = "start_page"
const val PREF_USER_RATES_START_TYPE = "user_rates_start_type"
const val PREF_USER_RATES_START_WATCH_STATUS = "user_rates_start_watch_status"

const val ACCESS_TOKEN = "access_token"
const val REFRESH_TOKEN = "refresh_token"
const val EXPIRES_IN = "expires_in"
const val CREATED_AT = "created_at"

const val ACCESS_TOKEN_LIB = "access_token_lib"
const val REFRESH_TOKEN_LIB = "refresh_token_lib"

const val USER_ID = "user_id"

const val BLANK = ""

// ======================================== Collections ============================================
val CACHE_LIST = listOf(16, 32, 64, 128, 256, 512)
val ROLES_RUSSIAN = setOf("Автор оригинала", "Режиссёр", "Сюжет", "Сюжет и иллюстрации", "Рисовка")

val EXTERNAL_LINK_KINDS = mapOf(
    "official_site" to Res.string.external_link_official_site,
    "wikipedia" to Res.string.external_link_wikipedia,
    "anime_news_network" to Res.string.external_link_anime_news_network,
    "myanimelist" to Res.string.external_link_myanimelist,
    "anime_db" to Res.string.external_link_anime_db,
    "world_art" to Res.string.external_link_world_art,
    "kinopoisk" to Res.string.external_link_kinopoisk,
    "kage_project" to Res.string.external_link_kage_project,
    "twitter" to Res.string.external_link_twitter,
    "smotret_anime" to Res.string.external_link_smotret_anime,
    "shiki" to Res.string.external_link_shiki,
    "amediateka" to Res.string.external_link_amediateka,
    "crunchyroll" to Res.string.external_link_crunchyroll,
    "amazon" to Res.string.external_link_amazon,
    "hidive" to Res.string.external_link_hidive,
    "hulu" to Res.string.external_link_hulu,
    "ivi" to Res.string.external_link_ivi,
    "kinopoisk_hd" to Res.string.external_link_kinopoisk_hd,
    "wink" to Res.string.external_link_wink,
    "netflix" to Res.string.external_link_netflix,
    "okko" to Res.string.external_link_okko,
    "youtube" to Res.string.external_link_youtube,
    "readmanga" to Res.string.external_link_readmanga,
    "mangalib" to Res.string.external_link_mangalib,
    "remanga" to Res.string.external_link_remanga,
    "mangaupdates" to Res.string.external_link_mangaupdates,
    "mangadex" to Res.string.external_link_mangadex,
    "mangafox" to Res.string.external_link_mangafox,
    "mangachan" to Res.string.external_link_mangachan,
    "mangahub" to Res.string.external_link_mangahub,
    "novel_tl" to Res.string.external_link_novel_tl,
    "ruranobe" to Res.string.external_link_ruranobe,
    "ranobelib" to Res.string.external_link_ranobelib,
    "novelupdates" to Res.string.external_link_novelupdates
)