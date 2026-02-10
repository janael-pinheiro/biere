package com.biere.catalog.infrastructure.adapter.input.rest.mappers

import com.biere.catalog.infrastructure.adapter.input.rest.dtos.CountryResponseDTO
import com.biere.catalog.domain.model.CountryModel

class CountryMapper {
    fun toCountryDto(countryModel: CountryModel): CountryResponseDTO {
        return CountryResponseDTO(countryModel.id, countryModel.name)
    }
}