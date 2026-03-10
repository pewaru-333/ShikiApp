package org.application.shikiapp.shared.utils.enums

import org.jetbrains.compose.resources.StringResource
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.season_autumn
import shikiapp.composeapp.generated.resources.season_spring
import shikiapp.composeapp.generated.resources.season_summer
import shikiapp.composeapp.generated.resources.season_winter
import java.time.Month

enum class Season(val months: List<Month>, val title: StringResource) {
    WINTER(listOf(Month.JANUARY, Month.FEBRUARY, Month.DECEMBER), Res.string.season_winter),
    SPRING(listOf(Month.MARCH, Month.APRIL, Month.MAY), Res.string.season_spring),
    SUMMER(listOf(Month.JUNE, Month.JULY, Month.AUGUST), Res.string.season_summer),
    AUTUMN(listOf(Month.SEPTEMBER, Month.OCTOBER, Month.NOVEMBER), Res.string.season_autumn)
}