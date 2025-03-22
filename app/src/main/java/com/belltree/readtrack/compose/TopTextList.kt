package com.belltree.readtrack.compose

import android.content.Context
import com.belltree.readtrack.R

enum class TopTextList(val resId: Int) {
    Home(R.string.app_topBar_home),
    Library(R.string.app_topBar_library),
    Setting(R.string.app_topBar_setting),
    RegisterProcess(R.string.app_topBar_register_process),
    Search(R.string.app_topBar_search),
    BookDetail(R.string.app_topBar_detail),
    MyBook(R.string.app_topBar_myBook);

    fun getValue(context: Context): String {
        return context.getString(resId)
    }
}