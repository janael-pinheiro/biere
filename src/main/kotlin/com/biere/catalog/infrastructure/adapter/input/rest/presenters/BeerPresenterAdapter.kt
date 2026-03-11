package com.biere.catalog.infrastructure.adapter.input.rest.presenters

import com.biere.catalog.infrastructure.adapter.input.rest.mappers.BeerMapper
import com.biere.catalog.infrastructure.adapter.input.rest.controllers.beer.BeerController
import com.biere.catalog.infrastructure.adapter.input.rest.controllers.brewery.BreweryController
import com.biere.catalog.infrastructure.adapter.input.rest.controllers.country.CountryController
import com.biere.catalog.infrastructure.adapter.input.rest.controllers.style.StyleController
import com.biere.catalog.infrastructure.adapter.input.rest.dtos.*
import com.biere.catalog.infrastructure.adapter.input.rest.helpers.withMethod
import com.biere.catalog.domain.port.output.BeerPresenterPort
import com.biere.catalog.domain.model.OutputBeerModel
import com.biere.catalog.domain.model.PaginatedResult
import com.opencsv.CSVWriter
import com.opencsv.bean.StatefulBeanToCsvBuilder
import org.springframework.core.io.ByteArrayResource
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*
import java.io.StringWriter
import java.nio.charset.StandardCharsets
import org.springframework.stereotype.Component

@Component
class BeerPresenterAdapter: BeerPresenterPort {
    override fun prepareJsonData(
        page: Pageable?,
        input: PaginatedResult<List<OutputBeerModel>>,
        uri: String): ApiCollectionResponseDTO<List<ApiIndividualResponseDTO<BeerResponseDTO>>>{
        val outputPage = PageDTO(
            totalPages = input.metadata.totalPages,
            totalElements = input.metadata.totalElements,
            next = input.metadata.current?.let {
                if (it < input.metadata.totalPages - 1)
                    linkTo(methodOn(BeerController::class.java).getBeersJson(PageRequest.of(input.metadata.next, page!!.pageSize, page.sort))).toUri().toString()
                else null
            },
            previous = input.metadata.current?.let {
                if (it > 0)
                    linkTo(methodOn(BeerController::class.java).getBeersJson(PageRequest.of(input.metadata.previous, page!!.pageSize, page.sort))).toUri().toString()
                else null
            },
            first = linkTo(methodOn(BeerController::class.java).getBeersJson(PageRequest.of(0, page!!.pageSize, page.sort))).toUri().toString(),
            last = linkTo(methodOn(BeerController::class.java).getBeersJson(PageRequest.of(input.metadata.last, page.pageSize, page.sort))).toUri().toString(),
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
        val beerDto = ApiIndividualResponseDTO(data = BeerMapper.mapToBeerResponseDTO(input))
        this.addSelfLink(beerDto)
        this.addUpdateLink(beerDto)
        this.addDeleteLink(beerDto)
        return beerDto
    }

    override fun prepareGetBeer(beerModel: OutputBeerModel): ApiIndividualResponseDTO<BeerResponseDTO> {
        val beerDto = ApiIndividualResponseDTO(data = BeerMapper.mapToBeerResponseDTO(beerModel))
        this.addSelfLink(beerDto)
        this.addUpdateLink(beerDto)
        this.addDeleteLink(beerDto)
        this.addBreweryLink(beerDto, beerModel)
        this.addStyleLink(beerDto, beerModel)
        this.addCountryLink(beerDto, beerModel)
        return beerDto
    }

    override fun prepareUpdateBrewery(beerModel: OutputBeerModel): ApiIndividualResponseDTO<BeerResponseDTO> {
        return this.prepareGetBeer(beerModel)
    }

    private fun addSelfLink(response: ApiIndividualResponseDTO<BeerResponseDTO>) {
        val selfLink = linkTo(methodOn(BeerController::class.java).getSpecificBeer(response.data.id))
            .withSelfRel()
            .andAffordance(afford(methodOn(BeerController::class.java).updateBeer(response.data.id, BeerUpdateRequestDTO(null, null, null, null, null))))
            .andAffordance(afford(methodOn(BeerController::class.java).deleteBeer(response.data.id)))
            .withMethod("GET")
        
        response.add(selfLink)
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

    private fun addBreweryLink(response: ApiIndividualResponseDTO<BeerResponseDTO>, beerModel: OutputBeerModel) {
        response.add(linkTo(methodOn(BreweryController::class.java)
            .getSpecificBrewery(beerModel.breweryId))
            .withRel("brewery")
            .withMethod("GET"))
    }

    private fun addStyleLink(response: ApiIndividualResponseDTO<BeerResponseDTO>, beerModel: OutputBeerModel) {
        response.add(linkTo(methodOn(StyleController::class.java)
            .getSpecificStyle(beerModel.styleId))
            .withRel("style")
            .withMethod("GET"))
    }

    private fun addCountryLink(response: ApiIndividualResponseDTO<BeerResponseDTO>, beerModel: OutputBeerModel) {
        response.add(linkTo(methodOn(CountryController::class.java)
            .getSpecificCountry(beerModel.countryId))
            .withRel("country")
            .withMethod("GET"))
    }

    private fun addCreateBeerLink(response: ApiCollectionResponseDTO<List<ApiIndividualResponseDTO<BeerResponseDTO>>>) {
        response.add(linkTo(methodOn(BeerController::class.java)
            .register(BeerRegistrationDTO("", 0F, 0, 0, 0)))
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