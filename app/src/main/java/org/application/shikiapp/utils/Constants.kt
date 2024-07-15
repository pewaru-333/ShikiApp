package org.application.shikiapp.utils


const val CLIENT_ID = "C0IlIBQYqt9VHjuoayfbBG9ulhBH9XWuTOxSX_6oE6g"
const val CLIENT_SECRET = "0U2MtkFgtGUP9_TFKBw1ORVy6S68KZDz_AdKsoMfnFM"
const val REDIRECT_URI = "org.application.shikiapp://"
const val GRANT_TYPE = "authorization_code"

const val ACCESS_TOKEN = "access_token"
const val REFRESH_TOKEN = "refresh_token"
const val EXPIRES_IN = "expires_in"
const val CREATED_AT = "created_at"
const val USER_ID = "user_id"


const val BLANK = ""

val ROLES_RUSSIAN = listOf("Автор оригинала", "Режиссёр")
val DATE_FORMATS = listOf("d.M.yyyy", "d.M", "yyyy")

val SEASONS = mapOf(
    "winter" to "Зима",
    "spring" to "Весна",
    "summer" to "Лето",
    "autumn" to "Осень"
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

val KINDS = mapOf(
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

val WATCH_STATUSES = mapOf(
    "planned" to "Запланировано",
    "watching" to "Смотрю",
    "rewatching" to "Пересматриваю",
    "completed" to "Просмотрено",
    "on_hold" to "Отложено",
    "dropped" to "Брошено"
)

val DURATIONS = mapOf(
    "S" to "До 10 минут",
    "D" to "До 30 минут",
    "F" to "Более 30 минут"
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

val STATUSES = mapOf(
    "anons" to "Анонсировано",
    "ongoing" to "Онгоинг",
    "released" to "Вышло"
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

val THEMES = listOf("Системная", "Светлая", "Тёмная")

enum class VideoKinds(val title: String, val kinds: List<String>) {
    Video("Видео", listOf("pv", "ed", "op", "op_ed_clip", "other")),
    Character("Трейлеры персонажей", listOf("character_trailer")),
    Episode("Превью эпизодов", listOf("episode_preview"))
}