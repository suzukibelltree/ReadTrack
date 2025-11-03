package com.belltree.readtrack.domain.model

data class LoginMessageResult(
    val message: String,
    val animationType: AnimationType
)

enum class AnimationType {
    STREAK_SMALL, // 連続ログイン更新！
    STREAK_MEDIUM,    // 連続ログイン大更新！
    STREAK_BIG, // 連続ログイン更新！
    NOTHING,   // 特にアニメーションなし
    RETURN,    // おかえりなさい！
}
