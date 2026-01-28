package com.msanwr.coursemate.jetpack.dev.ui.screen



import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.msanwr.coursemate.jetpack.dev.ui.component.CourseItem
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(LocalContext.current)
    )
) {
    val predictedCategory by homeViewModel.predictedCategory.collectAsState()
    val recommendedCourses by homeViewModel.recommendedCourses.collectAsState()

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val hasSearched by homeViewModel.hasSearched.collectAsState()
    LaunchedEffect(recommendedCourses, hasSearched) {
        if (hasSearched && recommendedCourses.isEmpty()) {
            Toast.makeText(context, "Model telah mencoba sekuat tenaga..", Toast.LENGTH_SHORT).show()
        }
    }
    Scaffold { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // Preference Test
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Preferensi Minat Anda",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))


                // Preference button
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showBottomSheet = true },
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Text(
                            text = if (predictedCategory == null) "Klik disini untuk mengisi preferensi anda" else "Klik lagi untuk mengisi ulang preferensi anda",
                            modifier = Modifier.weight(1f)
                        )
                        Icon(Icons.Default.ArrowForward, contentDescription = "Open Form")
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Course Recommendations
            if (recommendedCourses.isNotEmpty()) {
                item {
                    Text(
                        text = "Kursus yang Direkomendasikan:",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Predicted Category: $predictedCategory",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                // lazylist -> course result
                items(recommendedCourses, key = { it.id }) { course ->
                    CourseItem(
                        course = course,
                        onFavoriteClick = {
                            // fav toggle
                            homeViewModel.toggleFavoriteStatus(it)
                        })
                }
            }
        }

        // Bottom Sheet (form)
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState
            ) {
                PreferenceFormSheet(
                    homeViewModel = homeViewModel,
                    onSearchClicked = { subCategory, courseType, duration ->
                        homeViewModel.getRecommendations(subCategory, courseType, duration)
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                showBottomSheet = false
                            }
                        }
                    }
                )
            }
        }
    }
}