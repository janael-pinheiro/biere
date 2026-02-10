package com.biere.catalog.infrastructure.adapter.input.rest.mappers

import com.biere.catalog.infrastructure.adapter.input.rest.dtos.BreweryResponseDTO
import com.biere.catalog.infrastructure.adapter.input.rest.dtos.CountryResponseDTO
import com.biere.catalog.domain.model.BreweryModel

class BreweryMapper {
    fun toBreweryDto(breweryModel: BreweryModel): BreweryResponseDTO {
        return BreweryResponseDTO(
            id = breweryModel.id,
            name = breweryModel.name,
            country = CountryResponseDTO(id = breweryModel.country.id, name = breweryModel.country.name))
    }
}