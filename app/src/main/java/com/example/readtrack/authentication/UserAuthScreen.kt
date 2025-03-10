package com.example.readtrack.authentication

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.readtrack.R
import com.example.readtrack.Route
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

@Composable
fun FirebaseAuthScreen(
    navController: NavController,
    onLoginSuccess: (FirebaseUser?) -> Unit
) {
    val context = LocalContext.current
    val authLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Login 成功
            val user = FirebaseAuth.getInstance().currentUser
            onLoginSuccess(user)
            Log.d("FirebaseAuthScreen", "Logged in as: ${user?.displayName}")
            //ホーム画面へ遷移
            navController.navigate(Route.Home) {
                popUpTo(Route.Login) { inclusive = true }
            }
        } else {
            // Login 失敗
            onLoginSuccess(null)
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(R.drawable.login_background),
            contentDescription = "Login Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.3f)
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.weight(1f))
            val infiniteTransition = rememberInfiniteTransition(label = "infinite transition")
            val scale by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 1.2f,
                animationSpec = infiniteRepeatable(tween(3000), RepeatMode.Reverse),
                label = "scale"
            )
            Text(
                text = "ReadTrack",
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    transformOrigin = TransformOrigin.Center
                }
            )
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {
                    // まず、Firebaseのセッションを完全にクリア
                    FirebaseAuth.getInstance().signOut()
                    AuthUI.getInstance().signOut(context).addOnCompleteListener {
                        // Launch FirebaseUI Intent when button is clicked
                        val providers = listOf(AuthUI.IdpConfig.GoogleBuilder().build())
                        val signInIntent = AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .build()
                        authLauncher.launch(signInIntent)
                    }
                },
            ) {
                Text(text = "Googleアカウントでログインする")
            }
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}
