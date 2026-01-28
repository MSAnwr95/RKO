package com.msanwr.coursemate.jetpack.dev.ui.screen


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msanwr.coursemate.jetpack.dev.auth.GoogleAuthUiClient
import com.msanwr.coursemate.jetpack.dev.data.CourseRepository
import com.msanwr.coursemate.jetpack.dev.model.Course
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoritesViewModel(private val repository: CourseRepository, private val googleAuthUiClient: GoogleAuthUiClient) : ViewModel() {

    // data >> repository
    val favoriteCourses: StateFlow<List<Course>> = repository.favoriteCourses
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // toggle
    fun toggleFavoriteStatus(course: Course) {
        /*viewModelScope.launch {
            val updatedCourse = course.copy(isFavorite = !course.isFavorite)
            repository.updateCourse(updatedCourse)
        }*/
        googleAuthUiClient.getSignedInUser()?.userId?.let { userId ->
            viewModelScope.launch {
                repository.toggleFvouriteStatus(course, userId)
            }
        }
    }
}