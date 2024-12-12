package com.example.plzgod

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream
import android.util.Log

class DataProcessor(private val context: Context) {

    fun loadJsonFromAssets(fileName: String): JSONObject? {
        return try {
            val inputStream: InputStream = context.assets.open(fileName)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            val jsonString = String(buffer, Charsets.UTF_8)
            JSONObject(jsonString)
        } catch (ex: Exception) {
            Log.e("DataProcessor", "Error loading JSON file: $fileName", ex)
            null
        }
    }

    fun createHeatmapData(jsonFileName: String): Map<String, Any>? {
        val jsonData = loadJsonFromAssets(jsonFileName) ?: return null
        val dataArray = jsonData.getJSONArray("DATA")
        val heatmapPoints = mutableListOf<Map<String, Any>>()
        var maxVisitorCount = Int.MIN_VALUE

        for (i in 0 until dataArray.length()) {
            val data = dataArray.getJSONObject(i)
            val latitude = data.optDouble("latitude", Double.NaN)
            val longitude = data.optDouble("longitude", Double.NaN)
            val visitorCount = data.optInt("visitor_count", 0)

            if (latitude.isNaN() || longitude.isNaN()) {
                Log.w("DataProcessor", "Invalid data point at index $i: $data")
                continue
            }

            maxVisitorCount = maxOf(maxVisitorCount, visitorCount)
            heatmapPoints.add(
                mapOf(
                    "lat" to latitude,
                    "lng" to longitude,
                    "value" to visitorCount
                )
            )
        }

        Log.d("DataProcessor", "Loaded JSON data with ${dataArray.length()} points.")
        Log.d("DataProcessor", "Heatmap max value: $maxVisitorCount")

        return mapOf(
            "max" to maxVisitorCount,
            "data" to heatmapPoints
        )
    }
}
