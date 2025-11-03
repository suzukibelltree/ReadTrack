package com.belltree.readtrack.ui.home

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.belltree.readtrack.R
import com.belltree.readtrack.domain.model.AnimationType
import com.belltree.readtrack.domain.model.LoginMessageResult

@Composable
fun LoginMessageDialog(result: LoginMessageResult, onDismiss: () -> Unit) {
    val animationRes = when (result.animationType) {
        AnimationType.STREAK_SMALL -> R.raw.small
        AnimationType.STREAK_MEDIUM -> R.raw.medium
        AnimationType.STREAK_BIG -> R.raw.big
        AnimationType.RETURN -> R.raw.back
        AnimationType.NOTHING -> null
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = result.message, style = MaterialTheme.typography.titleLarge) },
        text = {
            if (animationRes != null) {
                val composition by rememberLottieComposition(
                    LottieCompositionSpec.RawRes(
                        animationRes
                    )
                )
                val progress by animateLottieCompositionAsState(composition)
                LottieAnimation(
                    composition = composition,
                    progress = { progress },
                    modifier = androidx.compose.ui.Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}
