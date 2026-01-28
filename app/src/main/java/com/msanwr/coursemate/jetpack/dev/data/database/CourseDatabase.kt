package com.msanwr.coursemate.jetpack.dev.data.database


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.msanwr.coursemate.jetpack.dev.model.Course
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.io.BufferedReader

@Database(entities = [Course::class], version = 1, exportSchema = false)
abstract class CourseDatabase : RoomDatabase() {

    abstract fun courseDao(): CourseDao

    companion object {
        @Volatile
        private var INSTANCE: CourseDatabase? = null

        fun getDatabase(context: Context): CourseDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CourseDatabase::class.java,
                    "course_database"
                )
                    .addCallback(DatabaseCallback(context))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }


    private class DatabaseCallback(private val context: Context) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    prePopulateDatabase(context, database.courseDao())
                }
            }
        }

        suspend fun prePopulateDatabase(context: Context, courseDao: CourseDao) {
            val inputStream = context.assets.open("courses.json")
            val bufferedReader = BufferedReader(inputStream.reader())
            val jsonString = bufferedReader.use { it.readText() }
            val jsonArray = JSONArray(jsonString)

            // obj: json >> course
            val courseList = mutableListOf<Course>()
            for (i in 0 until jsonArray.length()) {
                val courseJson = jsonArray.getJSONObject(i)
                val course = Course(
                    title = courseJson.getString("title"),
                    shortIntro = courseJson.getString("shortIntro"),
                    url = courseJson.getString("url"),
                    predictedCategory = courseJson.getString("predictedCategory")
                )
                courseList.add(course)
            }


            courseDao.insertAll(courseList)
        }
    }
}