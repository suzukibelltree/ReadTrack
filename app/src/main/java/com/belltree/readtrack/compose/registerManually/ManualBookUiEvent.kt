package com.belltree.readtrack.compose.registerManually

sealed class ManualBookUiEvent {
    data object CameraPermissionDenied : ManualBookUiEvent()
    data object ThumbnailSelectionCanceled : ManualBookUiEvent()
    data object BookSaved : ManualBookUiEvent()
}
