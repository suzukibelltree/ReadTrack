package com.belltree.readtrack.ui.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.belltree.readtrack.data.repository.SettingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val settingRepository: SettingRepository
) : ViewModel() {

    val enableNotification = settingRepository.notificationEnabled.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        false
    )

    private val _scheduleEvent = MutableSharedFlow<Boolean>()
    val scheduleEvent: SharedFlow<Boolean> = _scheduleEvent

    fun setNotificationEnabled(enable: Boolean) {
        viewModelScope.launch {
            settingRepository.setNotificationEnabled(enable)
            _scheduleEvent.emit(enable)
        }
    }

    fun applySettings() {
        viewModelScope.launch {
            _scheduleEvent.emit(enableNotification.value)
        }
    }
}

