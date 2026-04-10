package com.example.muniar

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hazards")
data class HazardEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val lat: Double,
    val lng: Double,

    val type: String,
    val description: String,

    val imageUri: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)