package com.biere.catalog.containers.api.controllers.country

import com.biere.catalog.containers.api.dtos.ApiCollectionResponseDTO
import com.biere.catalog.containers.api.dtos.ApiGeneralRegistrationMetadataDTO
import com.biere.catalog.containers.api.dtos.ApiGeneralRegistrationOperationsDTO
import com.biere.catalog.containers.api.dtos.ApiGeneralRegistrationResponseDTO
import com.biere.catalog.containers.api.dtos.CountryRegistrationDTO
import com.biere.catalog.containers.api.dtos.CountryResponseDTO
import com.biere.catalog.containers.api.dtos.MultipleCountriesResponseDTO
import com.biere.catalog.core.services.CountryService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI
import javax.print.attribute.standard.MediaName

@RestController
@RequestMapping("/v1/countries")
class CountryController(private val countryService: CountryService) {
    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun registerCountry(@RequestBody countryRegistrationDTO: CountryRegistrationDTO): ResponseEntity<ApiGeneralRegistrationResponseDTO<CountryResponseDTO>> {
        val country = this.countryService.registerCountry(countryRegistrationDTO)
        val links = listOf(
            "GET /v1/countries/${country.id}",
            "DELETE /v1/countries/${country.id}")
        return ResponseEntity.created(URI("")).body(ApiGeneralRegistrationResponseDTO(data = country, metadata = ApiGeneralRegistrationMetadataDTO(
            ApiGeneralRegistrationOperationsDTO(null, null, null))));
    }

    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getCountries(): ResponseEntity<ApiCollectionResponseDTO<List<CountryResponseDTO>>>{
        val countries = this.countryService.getCountries()
        return ResponseEntity.ok(ApiCollectionResponseDTO(data = countries, page = null))
    }

    @GetMapping("/{countryId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getSpecificCountry(@PathVariable countryId: Long): ResponseEntity<CountryResponseDTO> {
        val countryResponse = this.countryService.getSpecificCountry(countryId)
        return ResponseEntity.ok(countryResponse)
    }

    @DeleteMapping("/{countryId}")
    fun deleteCountry(@PathVariable countryId: Long): ResponseEntity<String> {
        this.countryService.deleteCountry(countryId)
        return ResponseEntity.noContent().build()
    }
}