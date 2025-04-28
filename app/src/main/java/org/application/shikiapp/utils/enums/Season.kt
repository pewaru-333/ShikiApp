package org.application.shikiapp.utils.enums

import androidx.annotation.StringRes
import org.application.shikiapp.R
import java.time.Month

enum class Season(val months: List<Month>, @StringRes val title: Int) {
    WINTER(listOf(Month.JANUARY, Month.FEBRUARY, Month.DECEMBER), R.string.season_winter),
    SPRING(listOf(Month.MARCH, Month.APRIL, Month.MAY), R.string.season_spring),
    SUMMER(listOf(Month.JUNE, Month.JULY, Month.AUGUST), R.string.season_summer),
    AUTUMN(listOf(Month.SEPTEMBER, Month.OCTOBER, Month.NOVEMBER), R.string.season_autumn)
}