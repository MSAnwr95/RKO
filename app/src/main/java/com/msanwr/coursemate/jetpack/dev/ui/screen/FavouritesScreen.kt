package com.msanwr.coursemate.jetpack.dev.ui.screen


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.msanwr.coursemate.jetpack.dev.ui.component.CourseItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    favoritesViewModel: FavoritesViewModel = viewModel(
        factory = FavoritesViewModelFactory(LocalContext.current)
    )
) {
    val favoriteCourses by favoritesViewModel.favoriteCourses.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kursus Favorit") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        if (favoriteCourses.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Anda belum menfavoritkan kursus.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
            ) {
                items(favoriteCourses, key = { it.id }) { course ->
                    CourseItem(
                        course = course,
                        onFavoriteClick = {
                            favoritesViewModel.toggleFavoriteStatus(it)
                        }
                    )
                }
            }
        }
    }
}