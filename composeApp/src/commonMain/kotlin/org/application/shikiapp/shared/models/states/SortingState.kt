package org.application.shikiapp.shared.models.states

import org.application.shikiapp.shared.utils.enums.OrderDirection
import org.application.shikiapp.shared.utils.enums.OrderRates

data class SortingState(
    val order: OrderRates = OrderRates.TITLE,
    val direction: OrderDirection = OrderDirection.ASCENDING
)
