package com.biere.catalog.containers.api.presenters

import com.biere.catalog.containers.api.controllers.country.CountryController
import com.biere.catalog.containers.api.dtos.ApiCollectionResponseDTO
import com.biere.catalog.containers.api.dtos.ApiIndividualResponseDTO
import com.biere.catalog.containers.api.dtos.CountryRegistrationDTO
import com.biere.catalog.containers.api.dtos.CountryResponseDTO
import com.biere.catalog.containers.api.dtos.CountryUpdateRequestDTO
import com.biere.catalog.containers.api.helpers.withMethod
import com.biere.catalog.containers.api.mappers.CountryMapper
import com.biere.catalog.core.models.CountryModel
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn

class CountryPresenterAdapter(private val countryMapper: CountryMapper = CountryMapper()) {
    fun prepareCreateCountry(countryModel: CountryModel): ApiIndividualResponseDTO<CountryResponseDTO> {
        val outputCountry = ApiIndividualResponseDTO(data = countryMapper.toCountryDto(countryModel))
        this.addSelfLink(outputCountry)
        this.addUpdateLink(outputCountry)
        this.addDeleteLink(outputCountry)
        this.addGetAllCountriesLink(outputCountry)
        return outputCountry
    }

    fun prepareGetCountry(countryModel: CountryModel): ApiIndividualResponseDTO<CountryResponseDTO> {
        val countryDto = ApiIndividualResponseDTO(data = countryMapper.toCountryDto(countryModel))
        this.addSelfLink(countryDto)
        this.addUpdateLink(countryDto)
        this.addDeleteLink(countryDto)
        return countryDto
    }

    fun prepareUpdateCountry(countryModel: CountryModel): ApiIndividualResponseDTO<CountryResponseDTO> {
        return this.prepareGetCountry(countryModel)
    }

    fun prepareGetAllCountries(countries: List<CountryModel>): ApiCollectionResponseDTO<List<ApiIndividualResponseDTO<CountryResponseDTO>>> {
        val response = countries.stream().map { country -> prepareGetCountry(country) }.toList()
        val outputCountries = ApiCollectionResponseDTO(data = response, page = null)
        this.addCreateCountryLink(outputCountries)
        return outputCountries
    }

    private fun addSelfLink(response: ApiIndividualResponseDTO<CountryResponseDTO>) {
        response.add(linkTo(methodOn(CountryController::class.java)
            .getSpecificCountry(response.data.id!!))
            .withSelfRel()
            .withMethod("GET"))
    }

    private fun addGetAllCountriesLink(response: ApiIndividualResponseDTO<CountryResponseDTO>) {
        response.add(linkTo(methodOn(CountryController::class.java)
            .getCountries())
            .withRel("get_all_countries")
            .withMethod("GET"))
    }

    private fun addUpdateLink(response: ApiIndividualResponseDTO<CountryResponseDTO>) {
        response.add(linkTo(methodOn(CountryController::class.java)
            .updateCountry(response.data.id!!, CountryUpdateRequestDTO("")))
            .withRel("update_country")
            .withMethod("PUT"))
    }

    private fun addDeleteLink(response: ApiIndividualResponseDTO<CountryResponseDTO>) {
        response.add(linkTo(methodOn(CountryController::class.java)
            .deleteCountry(response.data.id!!))
            .withRel("delete_country")
            .withMethod("DELETE"))
    }

    private fun addCreateCountryLink(response: ApiCollectionResponseDTO<List<ApiIndividualResponseDTO<CountryResponseDTO>>>) {
        response.add(linkTo(methodOn(CountryController::class.java)
            .registerCountry(CountryRegistrationDTO("")))
            .withRel("create_new_country")
            .withMethod("POST"))
    }
}