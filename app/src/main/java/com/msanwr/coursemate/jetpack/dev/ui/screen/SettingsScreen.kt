package com.msanwr.coursemate.jetpack.dev.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(LocalContext.current)
    ),
    onSignOut: () -> Unit,
    // onNavigateUpdateProfile: () -> Unit
) {
    val context = LocalContext.current
    val isDarkMode by settingsViewModel.isDarkMode.collectAsStateWithLifecycle()
    val userData by settingsViewModel.userData.observeAsState()

    var showLogoutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = userData) {
        if (userData == null) {
            Toast.makeText(context, "Successfully logged out", Toast.LENGTH_SHORT).show()
            onSignOut()
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text(text = "Konfirmasi Logout") },
            text = { Text(text = "Apakah Anda yakin ingin logout?") },
            confirmButton = {
                Button(
                    onClick = {
                        // signout confirm
                        settingsViewModel.signOut()
                        showLogoutDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Logout")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            userData?.let { user ->
                AsyncImage(
                    model = user.profilePictureUrl,
                    contentDescription = "Profile picture",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = user.email ?: "No email",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    // logout dialog
                    showLogoutDialog = true
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("LOGOUT")
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Mode Gelap", fontSize = 18.sp)
                Switch(
                    checked = isDarkMode,
                    onCheckedChange = { isEnabled ->
                        settingsViewModel.setDarkMode(isEnabled)
                    }
                )
            }
        }
    }
}