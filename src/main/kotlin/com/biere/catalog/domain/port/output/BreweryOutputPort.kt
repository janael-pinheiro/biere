package com.biere.catalog.domain.port.output

import com.biere.catalog.domain.model.BreweryModel

interface BreweryOutputPort {
    fun save(name: String, countryId: Long): BreweryModel
    fun findAll(): List<BreweryModel>
    fun findById(id: Long): BreweryModel
    fun update(id: Long, name: String, countryId: Long): BreweryModel
    fun delete(id: Long)
    fun existsByName(name: String): Boolean
    fun existsById(id: Long): Boolean
}
