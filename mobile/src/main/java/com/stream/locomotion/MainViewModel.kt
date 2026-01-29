package com.stream.locomotion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stream.locomotion.domain.usecase.ObserveConnectivity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    observeConnectivity: ObserveConnectivity
) : ViewModel() {

    val isOnline: StateFlow<Boolean> = observeConnectivity()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)
}
