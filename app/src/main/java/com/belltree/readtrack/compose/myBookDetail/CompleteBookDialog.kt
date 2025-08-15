package com.belltree.readtrack.compose.myBookDetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.belltree.readtrack.R

@Composable
fun CompleteBookDialog(
    bookThumbnailUrl: String?,
    onDismissRequest: () -> Unit,
    onPostToX: () -> Unit
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.confetti))
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever
    )

    // AlertDialogではなくDialogを使う
    Dialog(
        onDismissRequest = { /* ここでは閉じない */ },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(20.dp) // ← shapeをここで指定
                )
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "読了おめでとう！",
                fontSize = 20.sp,
                modifier = Modifier.padding(top = 16.dp)
            )


            Box(contentAlignment = Alignment.Center) {
                LottieAnimation(
                    composition = composition,
                    progress = { progress },
                    modifier = Modifier.size(240.dp)
                )

                AsyncImage(
                    model = bookThumbnailUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }


            Text(
                text = "読了をXにシェアできます！",
                fontSize = 16.sp,
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    onPostToX()
                    onDismissRequest()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text("Xに投稿")
            }


            OutlinedButton(
                onClick = { onDismissRequest() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),

                ) {
                Text("閉じる")
            }
        }
    }
}


@Preview
@Composable
fun CompleteBookDialogPreview() {
    CompleteBookDialog(
        bookThumbnailUrl = null,
        onDismissRequest = {},
        onPostToX = {}
    )
}