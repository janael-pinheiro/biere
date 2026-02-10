package com.biere.catalog.domain.port.input

import com.biere.catalog.domain.model.BreweryModel

interface BreweryUseCase {
    fun register(name: String, countryId: Long): BreweryModel
    fun getBreweries(): List<BreweryModel>
    fun getSpecificBrewery(breweryId: Long): BreweryModel
    fun updateBrewery(breweryId: Long, name: String, countryId: Long): BreweryModel
    fun deleteBrewery(breweryId: Long)
}
