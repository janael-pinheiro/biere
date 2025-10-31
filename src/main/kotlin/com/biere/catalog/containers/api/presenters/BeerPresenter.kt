package com.biere.catalog.containers.api.presenters

import com.biere.catalog.adapters.entities.BeerEntity
import com.biere.catalog.containers.api.dtos.ApiCollectionResponseDTO
import com.biere.catalog.containers.api.dtos.BeerResponseDTO
import com.biere.catalog.containers.api.dtos.PageDTO
import com.opencsv.CSVWriter
import com.opencsv.bean.StatefulBeanToCsvBuilder
import org.springframework.core.io.ByteArrayResource
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.io.StringWriter
import java.nio.charset.StandardCharsets

class BeerPresenter {
    fun prepareJsonData(page: Pageable, beers: Page<BeerEntity>): ApiCollectionResponseDTO<List<BeerResponseDTO>>{
        val page = PageDTO(
            totalPages = beers.totalPages,
            totalElements = beers.totalElements,
            next = "/v1/beers?page=${beers.nextOrLastPageable().pageNumber}&size=${page.pageSize}&sort=name",
            previous = "/v1/beers?page=${beers.previousOrFirstPageable().pageNumber}&size=${page.pageSize}&sort=name",
            first = "/v1/beers?page=0&size=${page.pageSize}&sort=name",
            last = "/v1/beers?page=${beers.totalPages - 1}&size=${page.pageSize}&sort=name"
        )
        val beersResponse = beers.stream().map { beer -> BeerResponseDTO(
            id = beer.id ?: 0,
            name = beer.name,
            countryName = beer.brewery.country.name,
            alcoholContent = beer.alcoholContent,
            brewery = beer.brewery.name,
            style = beer.style.name,
            year = beer.year) }.toList()
        return ApiCollectionResponseDTO(data = beersResponse, page = page)
    }

    fun prepareCsvData(beers: Page<BeerEntity>): ByteArrayResource {
        val writer = StringWriter()
        val beersResponse = beers.stream().map { beer -> BeerResponseDTO(
            id = beer.id ?: 0,
            name = beer.name,
            countryName = beer.brewery.country.name,
            alcoholContent = beer.alcoholContent,
            brewery = beer.brewery.name,
            style = beer.style.name,
            year = beer.year) }.toList()
        val beanToCsv = StatefulBeanToCsvBuilder<BeerResponseDTO>(writer)
            .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
            .withApplyQuotesToAll(false)
            .build()
        beanToCsv.write(beersResponse)
        return ByteArrayResource(writer.toString().toByteArray(StandardCharsets.UTF_8))
    }
}