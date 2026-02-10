package com.biere.catalog.domain.port.output

import com.biere.catalog.domain.model.BreweryModel
import com.biere.catalog.infrastructure.adapter.input.rest.dtos.ApiCollectionResponseDTO
import com.biere.catalog.infrastructure.adapter.input.rest.dtos.ApiIndividualResponseDTO
import com.biere.catalog.infrastructure.adapter.input.rest.dtos.BreweryResponseDTO

interface BreweryPresenterPort {
    fun prepareRegisterBrewery(breweryModel: BreweryModel): ApiIndividualResponseDTO<BreweryResponseDTO>
    fun prepareGetBrewery(breweryModel: BreweryModel): ApiIndividualResponseDTO<BreweryResponseDTO>
    fun prepareUpdateBrewery(breweryModel: BreweryModel): ApiIndividualResponseDTO<BreweryResponseDTO>
    fun prepareGetAllBreweries(breweries: List<BreweryModel>): ApiCollectionResponseDTO<List<ApiIndividualResponseDTO<BreweryResponseDTO>>>
}
