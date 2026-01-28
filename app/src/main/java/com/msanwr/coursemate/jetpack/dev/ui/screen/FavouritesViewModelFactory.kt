package com.msanwr.coursemate.jetpack.dev.ui.screen


import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.identity.Identity
import com.msanwr.coursemate.jetpack.dev.auth.GoogleAuthUiClient
import com.msanwr.coursemate.jetpack.dev.data.CourseRepository
import com.msanwr.coursemate.jetpack.dev.data.database.CourseDatabase

class FavoritesViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoritesViewModel::class.java)) {
            val database = CourseDatabase.getDatabase(context)
            val repository = CourseRepository(database.courseDao())
            val googleAuthUiClient = GoogleAuthUiClient(
                context = context.applicationContext,
                oneTapClient = Identity.getSignInClient(context.applicationContext)
            )
            @Suppress("UNCHECKED_CAST")
            return FavoritesViewModel(repository, googleAuthUiClient) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}