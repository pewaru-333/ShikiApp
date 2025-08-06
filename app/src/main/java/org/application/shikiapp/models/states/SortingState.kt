package org.application.shikiapp.models.states

import org.application.shikiapp.utils.enums.OrderDirection
import org.application.shikiapp.utils.enums.OrderRates

data class SortingState(
    val order: OrderRates = OrderRates.TITLE,
    val direction: OrderDirection = OrderDirection.ASCENDING
)
