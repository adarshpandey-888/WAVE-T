package com.example.muniar

class HazardRepository(private val dao: HazardDao) {

    suspend fun insert(hazard: HazardEntity) {
        dao.insertHazard(hazard)
    }

    suspend fun getAll(): List<HazardEntity> {
        return dao.getAllHazards()
    }
}