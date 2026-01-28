package com.msanwr.coursemate.jetpack.dev.ml


import android.content.Context
import android.util.Log
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.nio.ByteBuffer
import java.nio.ByteOrder

class TFLiteHelper(private val context: Context) {

    private var interpreter: Interpreter? = null

    // Mapping sub-kategori
    private val subCategoryMap = mapOf(
        "Machine Learning" to 20, "Data Analysis" to 9, "Business Essentials" to 4,
        "Software Development" to 37, "Finance" to 15, "Marketing" to 21,
        "Design and Product" to 11, "Cloud Computing" to 7, "Algorithms" to 0, //0 4 7   9 11 15   20 21 37
        "Animal Health" to 1, "Basic Science" to 2, "Biology" to 3, "Business Strategy" to 5, "Chemistry" to 6, "Computer Security and Networks" to 8, "Data Management" to 10,
        "Electrical Engineering" to 12, "Entrepreneurship" to 13, "Environmental Science and Sustainability" to 14, "Health Informatics" to 16, "Healthcare Management" to 17, "History" to 18, "Leadership and Management" to 19,
        "Mechanical Engineering" to 22, "Mobile and Web Development" to 23, "Music and Art" to 24, "Networking" to 25, "Nutrition" to 26, "Patient Care" to 27, "Personal Development" to 28, "Philosophy" to 29, "Physics and Astronomy" to 30,
        "Probability and Statistics" to 31, "Psychology" to 32, "Public Health" to 33, "Research" to 34, "Research Methods" to 35, "Security" to 36, "Software Development" to 37, "Support and Operations" to 38
    )

    private val courseTypeMap = mapOf(
        "Course" to 0, "Specialization" to 3,
        "Professional Certificate" to 1, "Project" to 2
    )

    private val categoryMap = mapOf(
        0 to "Arts and Humanities", 1 to "Business", 2 to "Computer Science",
        3 to "Data Science", 4 to "Health", 5 to "Information Technology",
        6 to "Personal Development", 7 to "Physical Science and Engineering"
    )

    // Nilai Mean dan Standard Deviation untuk penskalaan Durasi
    private val durationMean = 10.5f
    private val durationStdDev = 8.5f

    init {
        try {
            interpreter = Interpreter(loadModelFile("course_recommendation_model.tflite"))
        } catch (e: Exception) {
            Log.e("TFLiteHelper", "Error loading model", e)
        }
    }

    private fun loadModelFile(modelName: String): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd(modelName)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    fun getRecommendations(subCategory: String, courseType: String, duration: Int): String {
        if (interpreter == null) {
            return "Interpreter not initialized."
        }

        // PRE-PROCESSING
        // Mengubah input pengguna menjadi format yang diterima model
        val subCategoryEncoded = subCategoryMap[subCategory]?.toFloat() ?: 0.0f
        val courseTypeEncoded = courseTypeMap[courseType]?.toFloat() ?: 0.0f
        val durationScaled = (duration.toFloat() - durationMean) / durationStdDev

        // Membuat input buffer untuk model
        val inputBuffer = ByteBuffer.allocateDirect(1 * 3 * 4).apply {
            order(ByteOrder.nativeOrder())
            putFloat(subCategoryEncoded)
            putFloat(courseTypeEncoded)
            putFloat(durationScaled)
        }

        // OUTPUT BUFFER
        // Menyiapkan buffer untuk menampung hasil prediksi (ada 8 kategori)
        val outputBuffer = ByteBuffer.allocateDirect(1 * 8 * 4).apply {
            order(ByteOrder.nativeOrder())
        }

        // MENJALANKAN INFERENCE
        try {
            interpreter?.run(inputBuffer, outputBuffer)
        } catch (e: Exception) {
            Log.e("TFLiteHelper", "Error running inference", e)
            return "Error during inference."
        }

        // POST-PROCESSING
        // Mengambil hasil dari output buffer
        outputBuffer.rewind()
        val outputArray = FloatArray(8)
        outputBuffer.asFloatBuffer().get(outputArray)

        // Cari index probabilitas tertinggi
        val predictedIndex = outputArray.indices.maxByOrNull { outputArray[it] } ?: -1

        return categoryMap[predictedIndex] ?: "Unknown Category"
    }
}