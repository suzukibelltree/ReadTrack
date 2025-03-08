package com.example.readtrack.authentication

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
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
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.White, Color.Cyan),
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Welcome to ReadTrack!",
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                style = MaterialTheme.typography.headlineMedium,
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
