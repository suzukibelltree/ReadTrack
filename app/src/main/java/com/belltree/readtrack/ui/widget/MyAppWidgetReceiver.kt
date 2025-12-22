package com.belltree.readtrack.ui.widget

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.belltree.readtrack.data.repository.DatabaseBooksRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MyAppWidgetReceiver : GlanceAppWidgetReceiver() {
    @Inject
    lateinit var repository: DatabaseBooksRepository

    override val glanceAppWidget: GlanceAppWidget by lazy {
        MyAppWidget(repository)
    }
}