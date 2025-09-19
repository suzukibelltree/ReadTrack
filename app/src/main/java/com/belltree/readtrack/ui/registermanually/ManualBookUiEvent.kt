package com.belltree.readtrack.ui.registermanually

sealed class ManualBookUiEvent {
    data object CameraPermissionDenied : ManualBookUiEvent()
    data object ThumbnailSelectionCanceled : ManualBookUiEvent()
    data object BookSaved : ManualBookUiEvent()
}