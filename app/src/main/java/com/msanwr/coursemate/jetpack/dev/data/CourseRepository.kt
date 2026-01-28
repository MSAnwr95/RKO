package com.msanwr.coursemate.jetpack.dev.data


import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.msanwr.coursemate.jetpack.dev.data.database.CourseDao
import com.msanwr.coursemate.jetpack.dev.model.Course
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class CourseRepository(private val courseDao: CourseDao) {
    val allCourses: Flow<List<Course>> = courseDao.getAllCourses()
    suspend fun replaceAll(courses: List<Course>) {
        courseDao.deleteAll()
        courseDao.insertAll(courses)
    }
    fun getCoursesByCategory(category: String): Flow<List<Course>> {
        return courseDao.getCoursesByCategory(category)
    }
    suspend fun updateCourse(course: Course) {
        courseDao.updateCourse(course)
    }
    val favoriteCourses: Flow<List<Course>> = courseDao.getFavoriteCourses()


    private val firestore = Firebase.firestore
    suspend fun toggleFvouriteStatus(course: Course, userId: String) {
        val updatedCourse = course.copy(isFavorite = !course.isFavorite)

        // 1. Update database lokal (Room)
        courseDao.updateCourse(updatedCourse)

        val userDocRef = firestore.collection("users").document(userId)
        val courseId = course.id.toString() // ID dari Course object
        val fieldUpdate: FieldValue

        fieldUpdate = if (updatedCourse.isFavorite) {
            // tambah ID ke array
            FieldValue.arrayUnion(courseId)
        } else {
            // hapus ID dari array
            FieldValue.arrayRemove(courseId)
        }

        // map untuk data yang akan di-set
        val data = mapOf("favoriteCourseIds" to fieldUpdate)

        // .set dengan merge=true. buat dokumen jika tidak ada / update jika sudah ada.
        userDocRef.set(data, SetOptions.merge())
            //logcat debug
            .addOnSuccessListener {
                Log.d("CourseRepository", "Firestore favorite updated successfully for user: $userId")
            }
            .addOnFailureListener { e ->
                Log.w("CourseRepository", "Error updating firestore favorite for user: $userId", e)
            }
    }
    suspend fun syncFavoritesOnLogin(userId: String) = withContext(Dispatchers.IO) {
        try {
            val localCourses = allCourses.first()
            val favoriteIdsFromCloud = getFavoriteIdsFromFirestore(userId)

            val updatedLocalCourses = localCourses.map { course ->
                course.copy(isFavorite = favoriteIdsFromCloud.contains(course.id.toString()))
            }

            courseDao.updateCourses(updatedLocalCourses)
            Log.d("CourseRepository", "Favorites synced successfully.")
        } catch (e: Exception) {
            Log.e("CourseRepository", "Error syncing favorites", e)
        }
    }
    private suspend fun getFavoriteIdsFromFirestore(userId: String): List<String> = suspendCoroutine { continuation ->
        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val ids = document.get("favoriteCourseIds") as? List<String>
                    continuation.resume(ids ?: emptyList())
                } else {
                    continuation.resume(emptyList())
                }
            }
            .addOnFailureListener {
                continuation.resume(emptyList())
            }
    }

}
