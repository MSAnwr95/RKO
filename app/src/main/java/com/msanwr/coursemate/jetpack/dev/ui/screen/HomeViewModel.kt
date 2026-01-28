package com.msanwr.coursemate.jetpack.dev.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msanwr.coursemate.jetpack.dev.auth.GoogleAuthUiClient
import com.msanwr.coursemate.jetpack.dev.data.CourseRepository
import com.msanwr.coursemate.jetpack.dev.ml.TFLiteHelper
import com.msanwr.coursemate.jetpack.dev.model.Course
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(
    private val tfliteHelper: TFLiteHelper,
    private val repository: CourseRepository,
    private val googleAuthUiClient: GoogleAuthUiClient
) : ViewModel() {

    // PREFERENCE FORM OPTIONS
    val subCategoryOptions = listOf(
        "Machine Learning", "Data Analysis", "Business Essentials",
        "Software Development", "Finance", "Marketing",
        "Design and Product", "Cloud Computing", "Algorithms",
        "Animal Health", "Basic Science", "Business Strategy", "Chemistry", "Computer Security and Networks", " Data Management"
        , "Electrical Engineering", "Entrepreneurship", "Health Informatics", "Health Management", "History"
    )
    val courseTypeOptions = listOf("Course", "Specialization", "Professional Certificate", "Project")
    val durationOptions = listOf(4, 8, 16, 32)

    // State -> save kategori yang sedang dicari
    private val _searchQuery = MutableStateFlow<String?>(null)

    // update jika flow berubah
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val recommendedCourses: StateFlow<List<Course>> = _searchQuery.flatMapLatest { category ->
        if (category.isNullOrEmpty()) {
            emptyFlow()

        } else {
            repository.getCoursesByCategory(category) // data >> room
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val predictedCategory: StateFlow<String?> = _searchQuery.asStateFlow()
    private val _hasSearched = MutableStateFlow(false)
    val hasSearched = _hasSearched.asStateFlow()

    // run model, update query
    fun getRecommendations(subCategory: String, courseType: String, duration: Int) {
        _hasSearched.value = false
        viewModelScope.launch {
            val category = tfliteHelper.getRecommendations(subCategory, courseType, duration)
            _searchQuery.value = category
            _hasSearched.value = true
        }
    }

    fun toggleFavoriteStatus(course: Course) {
        // Hanya proses jika ada pengguna yang login
        googleAuthUiClient.getSignedInUser()?.userId?.let { userId ->
            viewModelScope.launch {
                repository.toggleFvouriteStatus(course, userId)
            }
        }
    }
    fun onLoginSuccess() {
        googleAuthUiClient.getSignedInUser()?.userId?.let { userId ->
            viewModelScope.launch {
                repository.syncFavoritesOnLogin(userId)
            }
        }
    }
}

