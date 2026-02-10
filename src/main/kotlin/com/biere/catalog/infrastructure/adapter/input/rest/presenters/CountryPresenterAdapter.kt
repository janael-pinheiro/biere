package com.biere.catalog.infrastructure.adapter.input.rest.presenters

import com.biere.catalog.infrastructure.adapter.input.rest.controllers.country.CountryController
import com.biere.catalog.infrastructure.adapter.input.rest.dtos.ApiCollectionResponseDTO
import com.biere.catalog.infrastructure.adapter.input.rest.dtos.ApiIndividualResponseDTO
import com.biere.catalog.infrastructure.adapter.input.rest.dtos.CountryRegistrationDTO
import com.biere.catalog.infrastructure.adapter.input.rest.dtos.CountryResponseDTO
import com.biere.catalog.infrastructure.adapter.input.rest.dtos.CountryUpdateRequestDTO
import com.biere.catalog.infrastructure.adapter.input.rest.helpers.withMethod
import com.biere.catalog.infrastructure.adapter.input.rest.mappers.CountryMapper
import com.biere.catalog.domain.model.CountryModel
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import com.biere.catalog.domain.port.output.CountryPresenterPort
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.afford
import org.springframework.stereotype.Component

@Component
class CountryPresenterAdapter(private val countryMapper: CountryMapper = CountryMapper()) : CountryPresenterPort {
    override fun prepareCreateCountry(countryModel: CountryModel): ApiIndividualResponseDTO<CountryResponseDTO> {
        val outputCountry = ApiIndividualResponseDTO(data = countryMapper.toCountryDto(countryModel))
        this.addSelfLink(outputCountry)
        this.addUpdateLink(outputCountry)
        this.addDeleteLink(outputCountry)
        this.addGetAllCountriesLink(outputCountry)
        return outputCountry
    }

    override fun prepareGetCountry(countryModel: CountryModel): ApiIndividualResponseDTO<CountryResponseDTO> {
        val countryDto = ApiIndividualResponseDTO(data = countryMapper.toCountryDto(countryModel))
        this.addSelfLink(countryDto)
        this.addUpdateLink(countryDto)
        this.addDeleteLink(countryDto)
        return countryDto
    }

    override fun prepareUpdateCountry(countryModel: CountryModel): ApiIndividualResponseDTO<CountryResponseDTO> {
        return this.prepareGetCountry(countryModel)
    }

    override fun prepareGetAllCountries(countries: List<CountryModel>): ApiCollectionResponseDTO<List<ApiIndividualResponseDTO<CountryResponseDTO>>> {
        val response = countries.stream().map { country -> prepareGetCountry(country) }.toList()
        val outputCountries = ApiCollectionResponseDTO(data = response, page = null)
        this.addCreateCountryLink(outputCountries)
        return outputCountries
    }

    private fun addSelfLink(response: ApiIndividualResponseDTO<CountryResponseDTO>) {
        val selfLink = linkTo(methodOn(CountryController::class.java).getSpecificCountry(response.data.id!!))
            .withSelfRel()
            .andAffordance(afford(methodOn(CountryController::class.java).updateCountry(response.data.id, CountryUpdateRequestDTO(""))))
            .andAffordance(afford(methodOn(CountryController::class.java).deleteCountry(response.data.id)))
            .withMethod("GET")
            
        response.add(selfLink)
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