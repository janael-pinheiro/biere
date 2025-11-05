package com.biere.catalog.core.boundaries.output

import com.biere.catalog.containers.api.dtos.ApiCollectionResponseDTO
import com.biere.catalog.containers.api.dtos.ApiGeneralRegistrationResponseDTO
import com.biere.catalog.containers.api.dtos.BeerResponseDTO
import com.biere.catalog.core.models.OutputBeerModel
import com.biere.catalog.core.models.PaginatedResult
import org.springframework.core.io.ByteArrayResource
import org.springframework.data.domain.Pageable

interface BeerPresenterOutputPort {
    fun prepareJsonData(page: Pageable, input: PaginatedResult<List<OutputBeerModel>>): ApiCollectionResponseDTO<List<BeerResponseDTO>>
    fun prepareCsvData(beers: PaginatedResult<List<OutputBeerModel>>): ByteArrayResource
    fun prepareRegistrationResponse(input: OutputBeerModel): ApiGeneralRegistrationResponseDTO<BeerResponseDTO>
}