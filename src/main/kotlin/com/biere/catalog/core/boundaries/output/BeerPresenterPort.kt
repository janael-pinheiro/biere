package com.biere.catalog.core.boundaries.output

import com.biere.catalog.containers.api.dtos.ApiCollectionResponseDTO
import com.biere.catalog.containers.api.dtos.ApiIndividualResponseDTO
import com.biere.catalog.containers.api.dtos.BeerResponseDTO
import com.biere.catalog.core.models.OutputBeerModel
import com.biere.catalog.core.models.PaginatedResult
import org.springframework.core.io.ByteArrayResource
import org.springframework.data.domain.Pageable

interface BeerPresenterPort {
    fun prepareJsonData(page: Pageable?, input: PaginatedResult<List<OutputBeerModel>>, uri: String): ApiCollectionResponseDTO<List<ApiIndividualResponseDTO<BeerResponseDTO>>>
    fun prepareCsvData(beers: PaginatedResult<List<OutputBeerModel>>): ByteArrayResource
    fun prepareRegistrationResponse(input: OutputBeerModel): ApiIndividualResponseDTO<BeerResponseDTO>
    fun prepareGetBeer(beerModel: OutputBeerModel): ApiIndividualResponseDTO<BeerResponseDTO>
    fun prepareUpdateBrewery(beerModel: OutputBeerModel): ApiIndividualResponseDTO<BeerResponseDTO>
}