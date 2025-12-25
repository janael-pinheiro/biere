package com.biere.catalog.containers.api.presenters

import com.biere.catalog.adapters.output.repositories.mappers.BeerMapper
import com.biere.catalog.containers.api.dtos.*
import com.biere.catalog.core.boundaries.output.BeerPresenterOutputPort
import com.biere.catalog.core.models.OutputBeerModel
import com.biere.catalog.core.models.PaginatedResult
import com.opencsv.CSVWriter
import com.opencsv.bean.StatefulBeanToCsvBuilder
import org.springframework.core.io.ByteArrayResource
import org.springframework.data.domain.Pageable
import java.io.StringWriter
import java.nio.charset.StandardCharsets

class BeerPresenterOutputAdapter: BeerPresenterOutputPort {
    override fun prepareJsonData(page: Pageable, input: PaginatedResult<List<OutputBeerModel>>): ApiCollectionResponseDTO<List<BeerResponseDTO>>{
        val outputPage = PageDTO(
            totalPages = input.metadata.totalPages,
            totalElements = input.metadata.totalElements,
            next = "/v1/beers?page=${input.metadata.next}&size=${page.pageSize}&sort=name",
            previous = "/v1/beers?page=${input.metadata.previous}&size=${page.pageSize}&sort=name",
            first = "/v1/beers?page=0&size=${page.pageSize}&sort=name",
            last = "/v1/beers?page=${input.metadata.last}&size=${page.pageSize}&sort=name",
            current = input.metadata.current
        )
        val beersResponse = input.data.stream().map(BeerMapper::mapToBeerResponseDTO).toList()
        return ApiCollectionResponseDTO(data = beersResponse, page = outputPage)
    }

    override fun prepareCsvData(beers: PaginatedResult<List<OutputBeerModel>>): ByteArrayResource {
        val writer = StringWriter()
        val beersResponse = beers.data.stream().map(BeerMapper::mapToBeerResponseDTO).toList()
        val beanToCsv = StatefulBeanToCsvBuilder<BeerResponseDTO>(writer)
            .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
            .withApplyQuotesToAll(false)
            .build()
        beanToCsv.write(beersResponse)
        return ByteArrayResource(writer.toString().toByteArray(StandardCharsets.UTF_8))
    }

    override fun prepareRegistrationResponse(input: OutputBeerModel): ApiGeneralRegistrationResponseDTO<BeerResponseDTO> {
        val outputBeer = BeerMapper.mapToBeerResponseDTO(input)

        return ApiGeneralRegistrationResponseDTO(data = outputBeer)
    }
}