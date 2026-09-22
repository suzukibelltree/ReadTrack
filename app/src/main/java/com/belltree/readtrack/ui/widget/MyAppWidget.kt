package com.belltree.readtrack.ui.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.Text
import com.belltree.readtrack.MainActivity
import com.belltree.readtrack.domain.usecase.GetRecentlyUpdatedBookUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL
import javax.inject.Inject

class MyAppWidget @Inject constructor(
    private val getRecentlyUpdatedBookUseCase: GetRecentlyUpdatedBookUseCase
) : GlanceAppWidget() {

    companion object {
        val BookIdKey = ActionParameters.Key<String>(MainActivity.EXTRA_BOOK_ID)
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val recentBook = getRecentlyUpdatedBookUseCase()

        val bitmap: Bitmap? = recentBook?.let {
            withContext(Dispatchers.IO) {
                val url = URL(it.thumbnail)
                BitmapFactory.decodeStream(url.openStream())
            }
        }

        val clickAction = if (recentBook != null) {
            actionStartActivity<MainActivity>(
                parameters = actionParametersOf(BookIdKey to recentBook.id)
            )
        } else {
            actionStartActivity<MainActivity>()
        }

        provideContent {
            Column(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .padding(8.dp)
                    .background(Color.White)
                    .clickable(clickAction),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (recentBook != null && bitmap != null) {
                    Text(text = "最近更新された本")
                    Image(
                        provider = ImageProvider(bitmap),
                        contentDescription = "最近更新された本のサムネイル",
                        modifier = GlanceModifier.width(60.dp)
                            .height(90.dp)
                    )
                    Text(text = recentBook.title, maxLines = 1)
                } else {
                    Text("最近更新された本はありません")
                }
            }
        }
    }

}
