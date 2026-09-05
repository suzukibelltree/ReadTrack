package com.belltree.readtrack.ui.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.actionStartActivity
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
import com.belltree.readtrack.R
import com.belltree.readtrack.data.repository.DatabaseBooksRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL
import javax.inject.Inject

class MyAppWidget @Inject constructor(
    private val repository: DatabaseBooksRepository
) : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val books = repository.getAllBooks()
        val recentBook = books.maxByOrNull { it.updatedDate }

        val bitmap: Bitmap? = recentBook?.let {
            withContext(Dispatchers.IO) {
                val url = URL(it.thumbnail)
                BitmapFactory.decodeStream(url.openStream())
            }
        }

        provideContent {
            Column(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .padding(8.dp)
                    .background(Color.White),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (recentBook != null && bitmap != null) {
                    Text(text = context.getString(R.string.widget_recentBook_title))
                    Image(
                        provider = ImageProvider(bitmap),
                        contentDescription = context.getString(R.string.widget_recentBook_thumbnail_description),
                        modifier = GlanceModifier.width(60.dp)
                            .height(90.dp)
                    )
                    Text(text = recentBook.title, maxLines = 1)
                } else {
                    Text(context.getString(R.string.widget_no_recentBook))
                }

                Button(
                    text = context.getString(R.string.widget_openApp),
                    onClick = actionStartActivity<MainActivity>()
                )
            }
        }
    }

}
