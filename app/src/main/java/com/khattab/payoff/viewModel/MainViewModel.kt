package com.khattab.payoff.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khattab.payoff.data.model.Market
import com.khattab.payoff.repository.MainRepository
import com.khattab.payoff.utils.Resource
import com.khattab.payoff.utils.networkState.AddMarketState
import com.khattab.payoff.utils.networkState.CityState
import com.khattab.payoff.utils.networkState.MarketState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private var mainRepository: MainRepository) : ViewModel() {

    private val _cities = MutableStateFlow(CityState())
    val cities: StateFlow<CityState> = _cities

    private val _market = MutableStateFlow(AddMarketState())
    val market: StateFlow<AddMarketState> = _market

    private val _markets = MutableStateFlow(MarketState())
    val markets: StateFlow<MarketState> = _markets

    fun getCitiesData() {
        viewModelScope.launch(Dispatchers.IO) {
            mainRepository.getCityData().onEach {
                when (it) {
                    is Resource.Loading -> {
                        _cities.value = CityState(isLoading = true)
                    }

                    is Resource.Error -> {
                        _cities.value = CityState(error = it.message ?: "")
                    }

                    is Resource.Success -> {
                        _cities.value = CityState(data = it.data)
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun addMarket(market: Market, id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            mainRepository.addMarket(market, id).onEach {
                when (it) {
                    is Resource.Loading -> {
                        _market.value = AddMarketState(isLoading = true)
                    }

                    is Resource.Error -> {
                        _market.value = AddMarketState(error = it.message ?: "")
                    }

                    is Resource.Success -> {
                        _market.value = AddMarketState(data = it.data)
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun getMarketData() {
        viewModelScope.launch(Dispatchers.IO) {
            mainRepository.getMarketData().onEach {
                when (it) {
                    is Resource.Loading -> {
                        _markets.value = MarketState(isLoading = true)
                    }

                    is Resource.Error -> {
                        _markets.value = MarketState(error = it.message ?: "")
                    }

                    is Resource.Success -> {
                        _markets.value = MarketState(data = it.data)
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

}