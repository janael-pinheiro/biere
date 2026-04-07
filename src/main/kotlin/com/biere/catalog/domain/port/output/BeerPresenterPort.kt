package com.biere.catalog.domain.port.output

import com.biere.catalog.infrastructure.adapter.input.rest.dtos.ApiCollectionResponseDTO
import com.biere.catalog.infrastructure.adapter.input.rest.dtos.ApiIndividualResponseDTO
import com.biere.catalog.infrastructure.adapter.input.rest.dtos.BeerResponseDTO
import com.biere.catalog.domain.model.OutputBeerModel
import com.biere.catalog.domain.model.PaginatedResult
import org.springframework.core.io.ByteArrayResource
import org.springframework.data.domain.Pageable

interface BeerPresenterPort {
    fun prepareJsonData(page: Pageable?, input: PaginatedResult<List<OutputBeerModel>>, uri: String): ApiCollectionResponseDTO<List<ApiIndividualResponseDTO<BeerResponseDTO>>>
    fun prepareCsvData(beers: PaginatedResult<List<OutputBeerModel>>): ByteArrayResource
    fun prepareRegistrationResponse(input: OutputBeerModel): ApiIndividualResponseDTO<BeerResponseDTO>
    fun prepareGetBeer(beerModel: OutputBeerModel): ApiIndividualResponseDTO<BeerResponseDTO>
    fun prepareUpdateBeer(beerModel: OutputBeerModel): ApiIndividualResponseDTO<BeerResponseDTO>
}