package com.biere.catalog.containers.api.mappers

import com.biere.catalog.containers.api.dtos.BreweryResponseDTO
import com.biere.catalog.containers.api.dtos.CountryResponseDTO
import com.biere.catalog.core.models.BreweryModel

class BreweryMapper {
    fun toBreweryDto(breweryModel: BreweryModel): BreweryResponseDTO {
        return BreweryResponseDTO(
            id = breweryModel.id,
            name = breweryModel.name,
            country = CountryResponseDTO(id = breweryModel.country.id, name = breweryModel.country.name))
    }
}