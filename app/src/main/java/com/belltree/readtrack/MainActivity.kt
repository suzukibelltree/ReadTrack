package com.belltree.readtrack

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateOf
import com.belltree.readtrack.app.AppInitializer
import com.belltree.readtrack.app.ReadTrackApp
import com.belltree.readtrack.ui.theme.ReadTrackTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val pendingDeepLinkBookId = mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        pendingDeepLinkBookId.value = consumeDeepLinkBookId(intent)
        setContent {
            ReadTrackTheme {
                AppInitializer {
                    ReadTrackApp(
                        pendingDeepLinkBookId = pendingDeepLinkBookId.value,
                        onDeepLinkHandled = { pendingDeepLinkBookId.value = null },
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        pendingDeepLinkBookId.value = consumeDeepLinkBookId(intent)
    }

    private fun consumeDeepLinkBookId(intent: Intent): String? {
        val bookId = intent.getStringExtra(EXTRA_BOOK_ID)
        intent.removeExtra(EXTRA_BOOK_ID)
        return bookId
    }

    companion object {
        const val EXTRA_BOOK_ID = "extra_book_id"
    }
}
