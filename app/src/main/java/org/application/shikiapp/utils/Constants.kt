package org.application.shikiapp.utils

import androidx.annotation.StringRes
import kotlinx.serialization.Serializable
import org.application.shikiapp.R
import org.application.shikiapp.R.drawable.vector_anime
import org.application.shikiapp.R.drawable.vector_calendar
import org.application.shikiapp.R.drawable.vector_character
import org.application.shikiapp.R.drawable.vector_home
import org.application.shikiapp.R.drawable.vector_manga
import org.application.shikiapp.R.drawable.vector_news
import org.application.shikiapp.R.drawable.vector_person
import org.application.shikiapp.R.drawable.vector_ranobe
import org.application.shikiapp.R.drawable.vector_settings
import org.application.shikiapp.R.string.text_achievements
import org.application.shikiapp.R.string.text_anime
import org.application.shikiapp.R.string.text_characters
import org.application.shikiapp.R.string.text_clubs
import org.application.shikiapp.R.string.text_friends
import org.application.shikiapp.R.string.text_manga
import org.application.shikiapp.R.string.text_mangaka
import org.application.shikiapp.R.string.text_mangakas
import org.application.shikiapp.R.string.text_others
import org.application.shikiapp.R.string.text_people
import org.application.shikiapp.R.string.text_producer
import org.application.shikiapp.R.string.text_ranobe
import org.application.shikiapp.R.string.text_seyu

// =========================================== Strings ============================================

const val CODE = "code"
const val CLIENT_ID = "C0IlIBQYqt9VHjuoayfbBG9ulhBH9XWuTOxSX_6oE6g"
const val CLIENT_SECRET = "0U2MtkFgtGUP9_TFKBw1ORVy6S68KZDz_AdKsoMfnFM"
const val REDIRECT_URI = "app://login"
const val GRANT_TYPE = "authorization_code"

const val ACCESS_TOKEN = "access_token"
const val REFRESH_TOKEN = "refresh_token"
const val EXPIRES_IN = "expires_in"
const val CREATED_AT = "created_at"
const val USER_ID = "user_id"

const val BASE_PATH = "*"
const val BLANK = ""

// =========================================== Lists ==============================================

val CACHE_LIST = listOf(16, 32, 64, 128, 256, 512)
val DATE_FORMATS = listOf("d.M.yyyy", "d.M", "yyyy")
val FAVOURITES_ITEMS = listOf(
    text_anime, text_manga, text_ranobe, text_characters, text_people,
    text_mangakas, text_seyu, text_others
)
val LINKED_KIND = listOf("common", "seyu", "mangaka", "producer", "person")
val LINKED_TYPE = listOf("Anime", "Manga", "Ranobe", "Person", "Character")
val ROLES_RUSSIAN = listOf("Автор оригинала", "Режиссёр", "Сюжет", "Сюжет и иллюстрации", "Рисовка")
val THEMES = listOf("Системная", "Светлая", "Тёмная")

// =========================================== Maps ===============================================

val DURATIONS = mapOf(
    "S" to "До 10 минут",
    "D" to "До 30 минут",
    "F" to "Более 30 минут"
)

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

val KINDS_A = mapOf(
    "tv" to "TV-сериал",
    "tv_13" to "Короткие",
    "tv_24" to "Средние",
    "tv_48" to "Длинные",
    "movie" to "Фильм",
    "ova" to "OVA",
    "ona" to "ONA",
    "special" to "Спецвыпуск",
    "tv_special" to "TV-спецвыпуск",
    "music" to "Клип",
    "pv" to "Проморолик",
    "cm" to "Реклама"
)

val KINDS_M = mapOf(
    "manga" to "Манга",
    "manhwa" to "Манхва",
    "manhua" to "Маньхуа",
    "one_shot" to "Ваншот",
    "doujin" to "Додзинси"
)

val KINDS_R = mapOf(
    "light_novel" to "Ранобэ",
    "novel" to "Новелла"
)

val ORDERS = mapOf(
    "id" to "По ID ↑",
    "id_desc" to "По ID ↓",
    "ranked" to "По оценке",
    "kind" to "По типу",
    "popularity" to "По популярности",
    "name" to "По названию",
    "aired_on" to "По дате выхода",
    "episodes" to "По эпизодам",
    "status" to "По статусу",
    "random" to "Случайно",
    "created_at" to "По дате создания ↑",
    "created_at_desc" to "По дате создания ↓"
)

val RATINGS = mapOf(
    "none" to "Без ограничений",
    "g" to "G",
    "pg" to "PG",
    "pg_13" to "PG",
    "r" to "R-17",
    "r_plus" to "R+",
    "rx" to "RX",
)

val SCORES = mapOf(
    0 to BLANK,
    1 to "1 — Хуже некуда",
    2 to "2 — Ужасно",
    3 to "3 — Очень плохо",
    4 to "4 — Плохо",
    5 to "5 — Более-менее",
    6 to "6 — Нормально",
    7 to "7 — Хорошо",
    8 to "8 — Отлично",
    9 to "9 — Великолепно",
    10 to "10 — Эпик вин!",
)

val SEASONS = mapOf(
    "winter" to "Зима",
    "spring" to "Весна",
    "summer" to "Лето",
    "autumn" to "Осень"
)

val STATUSES_A = mapOf(
    "anons" to "Анонсировано",
    "ongoing" to "Онгоинг",
    "released" to "Вышло"
)

val STATUSES_M = mapOf(
    "anons" to "Анонсировано",
    "ongoing" to "Сейчас издаётся",
    "released" to "Издано",
    "paused" to "Приостановлено",
    "discontinued" to "Прекращено"
)

val WATCH_STATUSES_A = mapOf(
    "planned" to "Запланировано",
    "watching" to "Смотрю",
    "rewatching" to "Пересматриваю",
    "completed" to "Просмотрено",
    "on_hold" to "Отложено",
    "dropped" to "Брошено"
)

val WATCH_STATUSES_M = mapOf(
    "planned" to "Запланировано",
    "watching" to "Читаю",
    "rewatching" to "Перечитываю",
    "completed" to "Прочитано",
    "on_hold" to "Отложено",
    "dropped" to "Брошено"
)

// =========================================== Enums ==============================================

enum class CatalogItems(@StringRes val title: Int, val icon: Int) {
    ANIME(text_anime, vector_anime),
    MANGA(text_manga, vector_manga),
    RANOBE(text_ranobe, vector_ranobe),
    CHARACTERS(text_characters, vector_character),
    PEOPLE(text_people, vector_person)
}

enum class Menu(val route: Any, @StringRes val title: Int, val icon: Int) {
    CATALOG(Catalog, R.string.text_catalog, vector_home),
    NEWS(News, R.string.text_news, vector_news),
    CALENDAR(Calendar, R.string.text_calendar, vector_calendar),
    PROFILE(Profile, R.string.text_profile, vector_character),
    SETTINGS(Settings, R.string.text_settings, vector_settings)
}

enum class PeopleFilterItems(@StringRes val title: Int) {
    SEYU(text_seyu), PRODUCER(text_producer), MANGAKA(text_mangaka)
}

enum class ProfileMenus(@StringRes val title: Int) {
    FRIENDS(text_friends), CLUBS(text_clubs), ACHIEVEMENTS(text_achievements)
}

enum class VideoKinds(val title: String, val kinds: List<String>) {
    VIDEO("Видео", listOf("pv", "ed", "op", "op_ed_clip", "other")),
    CHARACTER("Трейлеры персонажей", listOf("character_trailer")),
    EPISODE("Превью эпизодов", listOf("episode_preview"))
}

// =========================================== Screens =============================================
@Serializable
object Catalog

@Serializable
object News

@Serializable
object Calendar

@Serializable
object Profile

@Serializable
object Settings

@Serializable
data class Anime(val id: String)

@Serializable
data class Manga(val id: String)

@Serializable
data class Character(val id: String)

@Serializable
data class Person(val id: Long)

@Serializable
data class User(val id: Long)

@Serializable
data class Club(val id: Long)

@Serializable
data class NewsDetail(val id: Long)

@Serializable
data class AnimeRates(val id: Long)

@Serializable
data class MangaRates(val id: Long)