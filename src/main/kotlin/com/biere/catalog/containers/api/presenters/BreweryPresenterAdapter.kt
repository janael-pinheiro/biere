package com.biere.catalog.containers.api.presenters

import com.biere.catalog.containers.api.controllers.brewery.BreweryController
import com.biere.catalog.containers.api.dtos.*
import com.biere.catalog.containers.api.helpers.withMethod
import com.biere.catalog.containers.api.mappers.BreweryMapper
import com.biere.catalog.core.models.BreweryModel
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn

class BreweryPresenterAdapter(val breweryMapper: BreweryMapper = BreweryMapper()) {
    fun prepareRegisterBrewery(breweryModel: BreweryModel): ApiIndividualResponseDTO<BreweryResponseDTO> {
        val outputBrewery = ApiIndividualResponseDTO(data = this.breweryMapper.toBreweryDto(breweryModel))
        this.addSelfLink(outputBrewery)
        this.addUpdateLink(outputBrewery)
        this.addDeleteLink(outputBrewery)
        this.addGetAllBreweriesLink(outputBrewery)
        return outputBrewery
    }

    fun prepareGetBrewery(breweryModel: BreweryModel): ApiIndividualResponseDTO<BreweryResponseDTO> {
        val breweryDto = ApiIndividualResponseDTO(data = breweryMapper.toBreweryDto(breweryModel))
        this.addSelfLink(breweryDto)
        this.addUpdateLink(breweryDto)
        this.addDeleteLink(breweryDto)
        return breweryDto
    }

    fun prepareUpdateBrewery(breweryModel: BreweryModel): ApiIndividualResponseDTO<BreweryResponseDTO> {
        return this.prepareGetBrewery(breweryModel)
    }

    fun prepareGetAllBreweries(breweries: List<BreweryModel>): ApiCollectionResponseDTO<List<ApiIndividualResponseDTO<BreweryResponseDTO>>> {
        val response = breweries.map { brewery -> prepareGetBrewery(brewery) }
        val outputBreweries = ApiCollectionResponseDTO(data = response, page = null)
        this.addCreateBreweryLink(outputBreweries)
        return outputBreweries
    }

    private fun addSelfLink(response: ApiIndividualResponseDTO<BreweryResponseDTO>) {
        response.add(linkTo(methodOn(BreweryController::class.java)
            .getSpecificBrewery(response.data.id!!))
            .withSelfRel()
            .withMethod("GET"))
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

    private fun addCreateBreweryLink(response: ApiCollectionResponseDTO<List<ApiIndividualResponseDTO<BreweryResponseDTO>>>) {
        response.add(linkTo(methodOn(BreweryController::class.java)
            .register(BreweryRegistrationDTO("", 0)))
            .withRel("create_new_brewery")
            .withMethod("POST"))
    }
}