package com.msanwr.coursemate.jetpack.dev.ui.screen


import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.msanwr.coursemate.jetpack.dev.R
import com.msanwr.coursemate.jetpack.dev.ui.component.GoogleSignInButton


@Composable
fun LoginScreen(
    state: LoginState,
    onSignInClick: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(key1 = state.signInError) {
        state.signInError?.let { error ->
            if (!error.contains("cancelled", ignoreCase = true)) {
                Toast.makeText(context, error, Toast.LENGTH_LONG).show()
            }
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surfaceContainerLowest) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(25.dp),
               /* elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))*/
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Logo Aplikasi
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "App Logo",
                        modifier = Modifier.size(120.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Nama Aplikasi
                    Text(
                        text = stringResource(id = R.string.app_name),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(48.dp))

                    // Tombol Google Sign In
                    AnimatedContent(
                        targetState = state.isLoading,
                        label = "LoginButtonAnimation",
                        transitionSpec = {
                            fadeIn(animationSpec = tween(300)) +
                                    scaleIn(initialScale = 0.92f, animationSpec = tween(300)) togetherWith
                                    fadeOut(animationSpec = tween(300)) +
                                    scaleOut(targetScale = 0.92f, animationSpec = tween(300))
                        }
                    ) { isLoading ->
                        if (isLoading) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        } else {
                            GoogleSignInButton(
                                onClick = onSignInClick
                            )
                        }

                    }
                }
            }
        }
    }
}