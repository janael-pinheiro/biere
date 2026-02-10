package com.biere.catalog.infrastructure.adapter.input.rest.presenters

import com.biere.catalog.infrastructure.adapter.input.rest.controllers.brewery.BreweryController
import com.biere.catalog.infrastructure.adapter.input.rest.controllers.country.CountryController
import com.biere.catalog.infrastructure.adapter.input.rest.dtos.*
import com.biere.catalog.infrastructure.adapter.input.rest.helpers.withMethod
import com.biere.catalog.infrastructure.adapter.input.rest.mappers.BreweryMapper
import com.biere.catalog.domain.model.BreweryModel
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import com.biere.catalog.domain.port.output.BreweryPresenterPort
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.afford
import org.springframework.stereotype.Component

@Component
class BreweryPresenterAdapter(val breweryMapper: BreweryMapper = BreweryMapper()) : BreweryPresenterPort {
    override fun prepareRegisterBrewery(breweryModel: BreweryModel): ApiIndividualResponseDTO<BreweryResponseDTO> {
        val outputBrewery = ApiIndividualResponseDTO(data = this.breweryMapper.toBreweryDto(breweryModel))
        this.addSelfLink(outputBrewery)
        this.addUpdateLink(outputBrewery)
        this.addDeleteLink(outputBrewery)
        this.addCountryLink(outputBrewery, breweryModel)
        this.addGetAllBreweriesLink(outputBrewery)
        return outputBrewery
    }

    override fun prepareGetBrewery(breweryModel: BreweryModel): ApiIndividualResponseDTO<BreweryResponseDTO> {
        val breweryDto = ApiIndividualResponseDTO(data = breweryMapper.toBreweryDto(breweryModel))
        this.addSelfLink(breweryDto)
        this.addUpdateLink(breweryDto)
        this.addDeleteLink(breweryDto)
        this.addCountryLink(breweryDto, breweryModel)
        return breweryDto
    }

    override fun prepareUpdateBrewery(breweryModel: BreweryModel): ApiIndividualResponseDTO<BreweryResponseDTO> {
        return this.prepareGetBrewery(breweryModel)
    }

    override fun prepareGetAllBreweries(breweries: List<BreweryModel>): ApiCollectionResponseDTO<List<ApiIndividualResponseDTO<BreweryResponseDTO>>> {
        val response = breweries.map { brewery -> prepareGetBrewery(brewery) }
        val outputBreweries = ApiCollectionResponseDTO(data = response, page = null)
        this.addCreateBreweryLink(outputBreweries)
        return outputBreweries
    }

    private fun addSelfLink(response: ApiIndividualResponseDTO<BreweryResponseDTO>) {
        val selfLink = linkTo(methodOn(BreweryController::class.java).getSpecificBrewery(response.data.id!!))
            .withSelfRel()
            .andAffordance(afford(methodOn(BreweryController::class.java).updateCountry(response.data.id, BreweryUpdateRequestDTO("", 0))))
            .andAffordance(afford(methodOn(BreweryController::class.java).deleteBrewery(response.data.id)))
            .withMethod("GET")
            
        response.add(selfLink)
    }

    private fun addGetAllBreweriesLink(response: ApiIndividualResponseDTO<BreweryResponseDTO>) {
        response.add(linkTo(methodOn(BreweryController::class.java)
            .getBreweries())
            .withRel("get_all_breweries")
            .withMethod("GET"))
    }

    private fun addUpdateLink(response: ApiIndividualResponseDTO<BreweryResponseDTO>) {
        response.add(linkTo(methodOn(BreweryController::class.java)
            .updateCountry(response.data.id!!, BreweryUpdateRequestDTO("", 0)))
            .withRel("update_brewery")
            .withMethod("PUT"))
    }

    private fun addDeleteLink(response: ApiIndividualResponseDTO<BreweryResponseDTO>) {
        response.add(linkTo(methodOn(BreweryController::class.java)
            .deleteBrewery(response.data.id!!))
            .withRel("delete_brewery")
            .withMethod("DELETE"))
    }

    private fun addCountryLink(response: ApiIndividualResponseDTO<BreweryResponseDTO>, breweryModel: BreweryModel) {
        response.add(linkTo(methodOn(CountryController::class.java)
            .getSpecificCountry(breweryModel.country.id))
            .withRel("country")
            .withMethod("GET"))
    }

    private fun addCreateBreweryLink(response: ApiCollectionResponseDTO<List<ApiIndividualResponseDTO<BreweryResponseDTO>>>) {
        response.add(linkTo(methodOn(BreweryController::class.java)
            .register(BreweryRegistrationDTO("", 0)))
            .withRel("create_new_brewery")
            .withMethod("POST"))
    }
}