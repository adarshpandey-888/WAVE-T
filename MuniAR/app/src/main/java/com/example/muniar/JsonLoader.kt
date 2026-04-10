package com.example.muniar

import android.content.Context
import androidx.compose.ui.graphics.Color
import com.google.android.gms.maps.model.LatLng
import org.json.JSONObject

data class UtilityLine(
    val type: String,
    val label: String,
    val color: Color,
    val points: List<LatLng>
)

object JsonLoader {

    fun loadUtilities(context: Context, fileName: String): List<UtilityLine> {
        return try {

            val jsonString = context.assets.open(fileName)
                .bufferedReader()
                .use { it.readText() }

            val json = JSONObject(jsonString)
            val utilitiesArray = json.getJSONArray("utilities")

            val list = mutableListOf<UtilityLine>()

            for (i in 0 until utilitiesArray.length()) {

                val obj = utilitiesArray.getJSONObject(i)

                val type = obj.getString("type")
                val label = obj.getString("label")
                val colorHex = obj.getString("color")

                val pointsArray = obj.getJSONArray("points")
                val points = mutableListOf<LatLng>()

                for (j in 0 until pointsArray.length()) {
                    val point = pointsArray.getJSONObject(j)

                    points.add(
                        LatLng(
                            point.getDouble("lat"),
                            point.getDouble("lng")
                        )
                    )
                }

                list.add(
                    UtilityLine(
                        type = type,
                        label = label,
                        color = Color(android.graphics.Color.parseColor(colorHex)),
                        points = points
                    )
                )
            }

            list

        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}