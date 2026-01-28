package com.msanwr.coursemate.jetpack.dev

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.msanwr.coursemate.jetpack.dev.ui.component.BottomNavigationBar
import com.msanwr.coursemate.jetpack.dev.ui.screen.FavoritesScreen
import com.msanwr.coursemate.jetpack.dev.ui.screen.HomeScreen
import com.msanwr.coursemate.jetpack.dev.ui.screen.SettingsScreen
import com.msanwr.coursemate.jetpack.dev.ui.theme.MainAppTheme
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.msanwr.coursemate.jetpack.dev.data.ThemeDataStore


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.auth.api.identity.Identity
import com.msanwr.coursemate.jetpack.dev.auth.GoogleAuthUiClient
import com.msanwr.coursemate.jetpack.dev.auth.SignInResult
import com.msanwr.coursemate.jetpack.dev.ui.screen.HomeViewModel
import com.msanwr.coursemate.jetpack.dev.ui.screen.HomeViewModelFactory
import com.msanwr.coursemate.jetpack.dev.ui.screen.LoginScreen
import com.msanwr.coursemate.jetpack.dev.ui.screen.LoginViewModel
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {

    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        setContent {
            val themeDataStore = ThemeDataStore(LocalContext.current)
            val useDarkTheme by themeDataStore.isDarkMode.collectAsStateWithLifecycle(
                initialValue = isSystemInDarkTheme()
            )

            MainAppTheme(useDarkTheme = useDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(
                        // Mulai dari layar login jika pengguna belum masuk, jika sudah, langsung ke main_graph
                        startDestination = if(googleAuthUiClient.getSignedInUser() != null) "main_graph" else Screen.Login.route,
                        navController = navController
                    ) {
                        composable(Screen.Login.route) {
                            val viewModel = viewModel<LoginViewModel>()
                            val homeViewModel = viewModel<HomeViewModel>(factory = HomeViewModelFactory(applicationContext))
                            val state by viewModel.state.collectAsStateWithLifecycle()

                            // if login = SUCCESS
                            LaunchedEffect(key1 = state.isSignInSuccessful) {
                                if(state.isSignInSuccessful) {
                                    Toast.makeText(applicationContext, "Sign in successful", Toast.LENGTH_SHORT).show()
                                    homeViewModel.onLoginSuccess()
                                    navController.navigate("main_graph") {
                                        // Hapus riwayat navigasi login agar tidak bisa kembali
                                        popUpTo(Screen.Login.route) { inclusive = true }
                                    }
                                    viewModel.resetState()
                                }
                            }

                            // Peluncur untuk jendela pop-up Google Sign-In
                            val launcher = rememberLauncherForActivityResult(
                                contract = ActivityResultContracts.StartIntentSenderForResult(),
                                onResult = { result ->
                                    if(result.resultCode == RESULT_OK) {
                                        lifecycleScope.launch {
                                            val signInResult = googleAuthUiClient.signInWithIntent(
                                                intent = result.data ?: return@launch
                                            )
                                            viewModel.onSignInResult(signInResult)
                                        }
                                    } else {
                                        viewModel.onSignInResult(
                                            SignInResult(
                                                data = null,
                                                errorMessage = "Sign in canceled or failed"
                                            )
                                        )
                                    }
                                }
                            )

                            LoginScreen(
                                state = state,
                                onSignInClick = {
                                    viewModel.inSignInClick()
                                    lifecycleScope.launch {
                                        val signInIntentSender = googleAuthUiClient.signIn()
                                        launcher.launch(
                                            IntentSenderRequest.Builder(
                                                signInIntentSender ?: return@launch
                                            ).build()
                                        )
                                    }
                                }
                            )
                        }

                        // Grafik navigasi utama (setelah login)
                        composable("main_graph") {
                            MainScreen(
                                onSignOut = {
                                    // Navigasi ke layar login dan hapus semua riwayat
                                    navController.navigate(Screen.Login.route) {
                                        popUpTo(navController.graph.id) {
                                            inclusive = true
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    onSignOut: () -> Unit
) {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)

        ) {
            composable(Screen.Home.route) { HomeScreen() }
            composable(Screen.Favorites.route) { FavoritesScreen() }
            composable(Screen.Settings.route) { SettingsScreen(
                onSignOut = onSignOut,
            ) }

        }
    }
}
