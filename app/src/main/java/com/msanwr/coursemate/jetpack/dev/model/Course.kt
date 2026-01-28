package com.msanwr.coursemate.jetpack.dev.model


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "courses")
data class Course(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val shortIntro: String,
    val url: String,
    val predictedCategory: String,
    var isFavorite: Boolean = false
)