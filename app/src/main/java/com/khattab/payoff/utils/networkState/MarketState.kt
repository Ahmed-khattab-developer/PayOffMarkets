package com.khattab.payoff.utils.networkState

import com.khattab.payoff.data.model.Market


data class MarketState(
    val data: ArrayList<Market>? = null,
    val error: String = "",
    val isLoading: Boolean = false
)