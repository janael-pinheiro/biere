package com.biere.catalog.containers.api.controllers.country

import com.biere.catalog.core.dto.CountryRegistrationDTO
import com.biere.catalog.core.dto.CountryRegistrationResponseDTO
import com.biere.catalog.core.dto.CountryResponseDTO
import com.biere.catalog.core.dto.MultipleCountriesResponseDTO
import com.biere.catalog.core.services.CountryService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping("/v1/countries")
class CountryController(private val countryService: CountryService) {
    @PostMapping
    fun registerCountry(@RequestBody countryRegistrationDTO: CountryRegistrationDTO): ResponseEntity<CountryRegistrationResponseDTO> {
        val countryId: Long? = this.countryService.registerCountry(countryRegistrationDTO)
        return ResponseEntity.created(URI("")).body(
            CountryRegistrationResponseDTO(listOf(
                "GET /v1/countries/$countryId",
                "DELETE /v1/countries/$countryId")));
    }

    @GetMapping
    fun getCountries(): ResponseEntity<MultipleCountriesResponseDTO>{
        return ResponseEntity.ok(this.countryService.getCountries())
    }

    @GetMapping("/{countryId}")
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