package com.biere.catalog.containers.api.controllers.country

import com.biere.catalog.containers.api.dtos.*
import com.biere.catalog.core.services.CountryService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/v1/countries")
class CountryController(private val countryService: CountryService) {
    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun registerCountry(@RequestBody countryRegistrationDTO: CountryRegistrationDTO): ResponseEntity<ApiGeneralRegistrationResponseDTO<CountryResponseDTO>> {
        val country = this.countryService.registerCountry(countryRegistrationDTO)
//        val links = listOf(
//            "GET /v1/countries/${country.id}",
//            "DELETE /v1/countries/${country.id}")
        return ResponseEntity.created(URI("")).body(ApiGeneralRegistrationResponseDTO(data = country));
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

    @PutMapping("{countryId}", consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun updateCountry(@PathVariable countryId: Long, @RequestBody countryUpdateRequestDTO: CountryUpdateRequestDTO): ResponseEntity<CountryResponseDTO> {
        val country = this.countryService.updateCountry(countryId, countryUpdateRequestDTO)
        return ResponseEntity.ok(country)
    }
}