package com.biere.catalog.domain.port.output

import com.biere.catalog.domain.model.CountryModel
import com.biere.catalog.infrastructure.adapter.input.rest.dtos.ApiCollectionResponseDTO
import com.biere.catalog.infrastructure.adapter.input.rest.dtos.ApiIndividualResponseDTO
import com.biere.catalog.infrastructure.adapter.input.rest.dtos.CountryResponseDTO

interface CountryPresenterPort {
    fun prepareCreateCountry(countryModel: CountryModel): ApiIndividualResponseDTO<CountryResponseDTO>
    fun prepareGetCountry(countryModel: CountryModel): ApiIndividualResponseDTO<CountryResponseDTO>
    fun prepareUpdateCountry(countryModel: CountryModel): ApiIndividualResponseDTO<CountryResponseDTO>
    fun prepareGetAllCountries(countries: List<CountryModel>): ApiCollectionResponseDTO<List<ApiIndividualResponseDTO<CountryResponseDTO>>>
}
