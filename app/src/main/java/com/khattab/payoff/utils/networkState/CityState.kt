package com.khattab.payoff.utils.networkState

import com.khattab.payoff.data.model.City

data class CityState(
    val data: ArrayList<City>? = null,
    val error: String = "",
    val isLoading: Boolean = false
)