package com.belltree.readtrack.ui.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor() : ViewModel() {

    private val _enableNotification = MutableStateFlow(false)
    val enableNotification: StateFlow<Boolean> = _enableNotification

    private val _scheduleEvent = MutableSharedFlow<Boolean>()
    val scheduleEvent: SharedFlow<Boolean> = _scheduleEvent

    fun setNotificationEnabled(enabled: Boolean) {
        _enableNotification.value = enabled
    }

    fun applySettings() {
        viewModelScope.launch {
            _scheduleEvent.emit(_enableNotification.value)
        }
    }
}

