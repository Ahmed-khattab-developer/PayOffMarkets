package com.khattab.payoff.utils.networkState

import com.khattab.payoff.data.model.City

data class AddMarketState(
    val data: String? = null,
    val error: String = "",
    val isLoading: Boolean = false
)