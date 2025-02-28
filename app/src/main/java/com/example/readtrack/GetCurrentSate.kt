package com.example.readtrack

import android.annotation.SuppressLint
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter

// 現在の時刻を "yyyy/MM/dd/HH:mm" 形式の文字列で取得する関数
fun getCurrentFormattedTime(): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd/HH:mm")
    return LocalDateTime.now().format(formatter)
}

// 現在の年月を "yyyyMM" 形式の Int 型で取得する関数
@SuppressLint("DefaultLocale")
fun getCurrentYearMonthAsInt(): Int {
    val yearMonth = YearMonth.now()
    return "${yearMonth.year}${String.format("%02d", yearMonth.monthValue)}".toInt()
}