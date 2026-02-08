package com.biere.catalog.containers.api.controllers.country

import com.biere.catalog.containers.api.dtos.*
import com.biere.catalog.containers.api.presenters.CountryPresenterAdapter
import com.biere.catalog.core.services.CountryService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.Parameter
import jakarta.validation.Valid
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/v1/countries")
@Tag(name = "Countries", description = "Country management APIs")
class CountryController(private val countryService: CountryService, private val countryPresenter: CountryPresenterAdapter = CountryPresenterAdapter()) {
    @Operation(summary = "Register a new country", description = "Creates a new country.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "201", description = "Country created successfully"),
        ApiResponse(responseCode = "400", description = "Invalid input")
    ])
    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun registerCountry(@Valid @RequestBody countryRegistrationDTO: CountryRegistrationDTO): ResponseEntity<ApiIndividualResponseDTO<CountryResponseDTO>> {
        val country = this.countryService.registerCountry(countryRegistrationDTO)
        return ResponseEntity
            .created(URI("/v1/countries/${country.id}"))
            .body(countryPresenter.prepareCreateCountry(country));
    }

    @Operation(summary = "Get all countries", description = "Retrieves a list of all countries.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Successfully retrieved list")
    ])
    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getCountries(): ResponseEntity<ApiCollectionResponseDTO<List<ApiIndividualResponseDTO<CountryResponseDTO>>>>{
        val countries = this.countryService.getCountries()
        return ResponseEntity.ok(this.countryPresenter.prepareGetAllCountries(countries))
    }

    @Operation(summary = "Get a specific country", description = "Retrieves details of a specific country by ID.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Successfully retrieved country"),
        ApiResponse(responseCode = "404", description = "Country not found")
    ])
    @GetMapping("/{countryId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getSpecificCountry(
        @Parameter(description = "ID of the country to be retrieved", example = "1")
        @PathVariable countryId: Long
    ): ResponseEntity<ApiIndividualResponseDTO<CountryResponseDTO>> {
        val countryResponse = this.countryService.getSpecificCountry(countryId)
        return ResponseEntity.ok(this.countryPresenter.prepareGetCountry(countryResponse))
    }

    @Operation(summary = "Delete a country", description = "Deletes a country by ID.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "204", description = "Country deleted successfully"),
        ApiResponse(responseCode = "404", description = "Country not found")
    ])
    @DeleteMapping("/{countryId}")
    fun deleteCountry(
        @Parameter(description = "ID of the country to be deleted", example = "1")
        @PathVariable countryId: Long
    ): ResponseEntity<String> {
        this.countryService.deleteCountry(countryId)
        return ResponseEntity.noContent().build()
    }

    @Operation(summary = "Update a country", description = "Updates an existing country by ID.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Country updated successfully"),
        ApiResponse(responseCode = "404", description = "Country not found")
    ])
    @PutMapping("{countryId}", consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun updateCountry(
        @Parameter(description = "ID of the country to be updated", example = "1")
        @PathVariable countryId: Long,
        @Valid @RequestBody countryUpdateRequestDTO: CountryUpdateRequestDTO
    ): ResponseEntity<ApiIndividualResponseDTO<CountryResponseDTO>> {
        val country = this.countryService.updateCountry(countryId, countryUpdateRequestDTO)
        return ResponseEntity.ok(this.countryPresenter.prepareUpdateCountry(country))
    }
}