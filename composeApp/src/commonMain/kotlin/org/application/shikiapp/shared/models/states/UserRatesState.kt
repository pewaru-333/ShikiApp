package org.application.shikiapp.shared.models.states

import org.application.shikiapp.shared.utils.BLANK
import org.application.shikiapp.shared.utils.enums.OrderDirection
import org.application.shikiapp.shared.utils.enums.OrderRates

data class UserRatesState(
    val search: String = BLANK,
    val order: OrderRates = OrderRates.TITLE,
    val direction: OrderDirection = OrderDirection.ASCENDING,
    val isRefreshing: Boolean = false,
    val showDialog: Boolean = false
)
