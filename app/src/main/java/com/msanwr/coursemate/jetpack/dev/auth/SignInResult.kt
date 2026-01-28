package com.msanwr.coursemate.jetpack.dev.auth


// Menyimpan data pengguna yang sudah login
data class UserData(
    val userId: String,
    val username: String?,
    val profilePictureUrl: String?,
    val email: String?
)

// Menyimpan status hasil dari proses login
data class SignInResult(
    val data: UserData?,
    val errorMessage: String?
)