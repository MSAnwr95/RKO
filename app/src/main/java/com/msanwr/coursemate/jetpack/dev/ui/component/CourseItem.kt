package com.msanwr.coursemate.jetpack.dev.ui.component


import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.msanwr.coursemate.jetpack.dev.model.Course
import androidx.compose.material3.SmallFloatingActionButton

@Composable
fun CourseItem(
    course: Course,
    onFavoriteClick: (Course) -> Unit) {
    val context = LocalContext.current


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Kolom Teks (Judul & Deskripsi)
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = course.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = course.shortIntro,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Kolom FABs
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Tombol Favorit
                SmallFloatingActionButton(
                    onClick = {
                        onFavoriteClick(course)
                    },
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Icon(
                        // fav vector
                        imageVector = if (course.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Tombol Tautan
                SmallFloatingActionButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(course.url))
                        context.startActivity(intent)
                    },
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Go to course"
                    )
                }

            }
        }
    }
}