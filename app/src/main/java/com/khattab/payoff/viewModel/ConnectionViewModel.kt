package com.khattab.payoff.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.khattab.payoff.repository.ConnectivityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ConnectionViewModel @Inject constructor(connectivityRepository: ConnectivityRepository) :
    ViewModel() {
    val isOnline = connectivityRepository.isConnected.asLiveData()
}