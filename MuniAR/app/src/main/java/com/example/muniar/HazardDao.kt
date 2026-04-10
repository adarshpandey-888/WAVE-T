package com.example.muniar

import androidx.room.*

@Dao
interface HazardDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHazard(hazard: HazardEntity)

    @Query("SELECT * FROM hazards")
    suspend fun getAllHazards(): List<HazardEntity>

    @Delete
    suspend fun deleteHazard(hazard: HazardEntity)
}