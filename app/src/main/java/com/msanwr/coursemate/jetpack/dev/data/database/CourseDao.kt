package com.msanwr.coursemate.jetpack.dev.data.database


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.msanwr.coursemate.jetpack.dev.model.Course
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(courses: List<Course>)

    @Query("SELECT * FROM courses")
    fun getAllCourses(): Flow<List<Course>>

    @Query("SELECT * FROM courses WHERE predictedCategory = :category LIMIT 10")
    fun getCoursesByCategory(category: String): Flow<List<Course>>

    @Query("DELETE FROM courses")
    suspend fun deleteAll()

    // untuk favourite
    @Update
    suspend fun updateCourse(course: Course)

    @Query("SELECT * FROM courses WHERE isFavorite = 1")
    fun getFavoriteCourses(): Flow<List<Course>>

    @Update
    suspend fun updateCourses(courses: List<Course>)
}