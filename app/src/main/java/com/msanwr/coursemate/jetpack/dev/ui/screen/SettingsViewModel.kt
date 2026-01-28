package com.msanwr.coursemate.jetpack.dev.ui.screen


import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.msanwr.coursemate.jetpack.dev.data.ThemeDataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FirebaseAuth
import com.msanwr.coursemate.jetpack.dev.auth.GoogleAuthUiClient
import com.msanwr.coursemate.jetpack.dev.auth.UserData

class SettingsViewModel(private val themeDataStore: ThemeDataStore, private val googleAuthUiClient: GoogleAuthUiClient) : ViewModel() {

    // dark mode
    val isDarkMode: StateFlow<Boolean> = themeDataStore.isDarkMode
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )
    fun setDarkMode(isEnabled: Boolean) {
        viewModelScope.launch {
            themeDataStore.setDarkMode(isEnabled)
        }
    }

    //login (livedata)
    private val _userData = MutableLiveData<UserData?>()
    val userData: LiveData<UserData?> = _userData
    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null) {
            _userData.value = UserData(
                userId = firebaseUser.uid,
                username = firebaseUser.displayName,
                profilePictureUrl = firebaseUser.photoUrl?.toString(),
                email = firebaseUser.email
            )
        } else {
            _userData.value = null
        }
    }

    // viewmodel -> firebase auth
    init {
        // listen perubanah state
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener)
    }

    // proteksi memori leak saat viewmodel dihapus
    override fun onCleared() {
        super.onCleared()
        FirebaseAuth.getInstance().removeAuthStateListener(authStateListener)
    }

    fun signOut() {
        viewModelScope.launch {
            googleAuthUiClient.signOut()
        }
    }
}
// Factory viewmodelnya
class SettingsViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            val themeDataStore = ThemeDataStore(context.applicationContext)
            val googleAuthUiClient = GoogleAuthUiClient(
                context = context.applicationContext,
                oneTapClient = Identity.getSignInClient(context.applicationContext)
            )
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(themeDataStore, googleAuthUiClient) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

