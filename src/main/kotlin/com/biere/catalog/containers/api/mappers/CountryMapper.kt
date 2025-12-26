package com.biere.catalog.containers.api.mappers

import com.biere.catalog.containers.api.dtos.CountryResponseDTO
import com.biere.catalog.core.models.CountryModel

class CountryMapper {
    fun toCountryDto(countryModel: CountryModel): CountryResponseDTO {
        return CountryResponseDTO(countryModel.id, countryModel.name)
    }
}