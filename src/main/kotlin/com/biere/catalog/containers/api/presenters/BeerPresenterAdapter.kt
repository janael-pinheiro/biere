package com.biere.catalog.containers.api.presenters

import com.biere.catalog.adapters.output.repositories.mappers.BeerMapper
import com.biere.catalog.containers.api.controllers.beer.BeerController
import com.biere.catalog.containers.api.controllers.brewery.BreweryController
import com.biere.catalog.containers.api.controllers.country.CountryController
import com.biere.catalog.containers.api.controllers.style.StyleController
import com.biere.catalog.containers.api.dtos.*
import com.biere.catalog.containers.api.helpers.withMethod
import com.biere.catalog.core.boundaries.output.BeerPresenterPort
import com.biere.catalog.core.models.OutputBeerModel
import com.biere.catalog.core.models.PaginatedResult
import com.opencsv.CSVWriter
import com.opencsv.bean.StatefulBeanToCsvBuilder
import org.springframework.core.io.ByteArrayResource
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import java.io.StringWriter
import java.nio.charset.StandardCharsets

class BeerPresenterAdapter: BeerPresenterPort {
    override fun prepareJsonData(
        page: Pageable?,
        input: PaginatedResult<List<OutputBeerModel>>,
        uri: String): ApiCollectionResponseDTO<List<ApiIndividualResponseDTO<BeerResponseDTO>>>{
        val outputPage = PageDTO(
            totalPages = input.metadata.totalPages,
            totalElements = input.metadata.totalElements,
            next = "${uri}?page=${input.metadata.next}&size=${page!!.pageSize}&sort=name",
            previous = "${uri}?page=${input.metadata.previous}&size=${page.pageSize}&sort=name",
            first = "${uri}?page=0&size=${page.pageSize}&sort=name",
            last = "${uri}?page=${input.metadata.last}&size=${page.pageSize}&sort=name",
            current = input.metadata.current
        )
        val beersResponse = input.data.stream().map { beer -> prepareGetBeer(beer) }.toList()
        val output = ApiCollectionResponseDTO(data = beersResponse, page = outputPage)
        this.addCreateBeerLink(output)
        this.addGetAllBreweriesLink(output)
        this.addGetAllCountriesLink(output)
        this.addGetAllStylesLink(output)
        return output
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

    override fun prepareRegistrationResponse(input: OutputBeerModel): ApiIndividualResponseDTO<BeerResponseDTO> {
        val outputBeer = BeerMapper.mapToBeerResponseDTO(input)
        return ApiIndividualResponseDTO(data = outputBeer)
    }

    override fun prepareGetBeer(beerModel: OutputBeerModel): ApiIndividualResponseDTO<BeerResponseDTO> {
        val beerDto = ApiIndividualResponseDTO(data = BeerMapper.mapToBeerResponseDTO(beerModel))
        this.addSelfLink(beerDto)
        this.addUpdateLink(beerDto)
        this.addDeleteLink(beerDto)
        return beerDto
    }

    override fun prepareUpdateBrewery(beerModel: OutputBeerModel): ApiIndividualResponseDTO<BeerResponseDTO> {
        return this.prepareGetBeer(beerModel)
    }

    private fun addSelfLink(response: ApiIndividualResponseDTO<BeerResponseDTO>) {
        response.add(linkTo(methodOn(BeerController::class.java)
            .getSpecificBeer(response.data.id))
            .withSelfRel()
            .withMethod("GET"))
    }

    private fun addGetAllBeersLink(
        response: ApiIndividualResponseDTO<BeerResponseDTO>,
        page: Pageable = PageRequest.of(0, 0, Sort.unsorted())) {
        response.add(linkTo(methodOn(BeerController::class.java)
            .getBeersJson(page))
            .withRel("get_all_beers")
            .withMethod("GET"))
    }

    private fun addUpdateLink(response: ApiIndividualResponseDTO<BeerResponseDTO>) {
        response.add(linkTo(methodOn(BeerController::class.java)
            .updateBeer(response.data.id, BeerUpdateRequestDTO("", 0F, 0, 0, 0)))
            .withRel("update_beer")
            .withMethod("PATCH"))
    }

    private fun addDeleteLink(response: ApiIndividualResponseDTO<BeerResponseDTO>) {
        response.add(linkTo(methodOn(BeerController::class.java)
            .deleteBeer(response.data.id))
            .withRel("delete_beer")
            .withMethod("DELETE"))
    }

    private fun addCreateBeerLink(response: ApiCollectionResponseDTO<List<ApiIndividualResponseDTO<BeerResponseDTO>>>) {
        response.add(linkTo(methodOn(BeerController::class.java)
            .register(BeerRegistrationDTO("", 0, 0F, 0, 0, 0)))
            .withRel("create_new_beer")
            .withMethod("POST"))
    }

    private fun addGetAllBreweriesLink(response: ApiCollectionResponseDTO<List<ApiIndividualResponseDTO<BeerResponseDTO>>>) {
        response.add(linkTo(methodOn(BreweryController::class.java)
            .getBreweries())
            .withRel("get_all_breweries")
            .withMethod("GET"))
    }

    private fun addGetAllCountriesLink(response: ApiCollectionResponseDTO<List<ApiIndividualResponseDTO<BeerResponseDTO>>>) {
        response.add(linkTo(methodOn(CountryController::class.java)
            .getCountries())
            .withRel("get_all_countries")
            .withMethod("GET"))
    }

    private fun addGetAllStylesLink(response: ApiCollectionResponseDTO<List<ApiIndividualResponseDTO<BeerResponseDTO>>>) {
        response.add(linkTo(methodOn(StyleController::class.java)
            .getStyles())
            .withRel("get_all_styles")
            .withMethod("GET"))
    }
}