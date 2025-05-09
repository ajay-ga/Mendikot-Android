package com.mendikotapp.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class PlayerSetupState(
    val humanPlayerName: String = "",
    val botNames: List<String> = listOf("Bot 1", "Bot 2", "Bot 3"),
    val isValidName: Boolean = false
)

@HiltViewModel
class PlayerSetupViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow(PlayerSetupState())
    val state: StateFlow<PlayerSetupState> = _state.asStateFlow()

    fun updatePlayerName(name: String) {
        _state.value = _state.value.copy(
            humanPlayerName = name,
            isValidName = name.trim().length >= 2
        )
    }
} 