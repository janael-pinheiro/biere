package com.biere.catalog.infrastructure.adapter.input.rest.controllers.brewery

import com.biere.catalog.infrastructure.adapter.input.rest.dtos.*
import com.biere.catalog.infrastructure.adapter.input.rest.presenters.BreweryPresenterAdapter
import com.biere.catalog.domain.exception.NotFoundException
import com.biere.catalog.domain.port.input.BreweryUseCase
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.security.access.prepost.PreAuthorize
import java.net.URI
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.hateoas.MediaTypes
import com.biere.catalog.infrastructure.adapter.input.rest.controllers.Scopes

import com.biere.catalog.domain.port.output.BreweryPresenterPort

@RestController
@RequestMapping("/v1/breweries")
@Tag(name = "Breweries", description = "Brewery management APIs")
class BreweryController(private val breweryService: BreweryUseCase, private val breweryPresenter: BreweryPresenterPort) {
    @Operation(
        summary = "Register a new brewery",
        description = "Registers a new brewery. A valid 'country_id' is required. Obtain valid countries via 'GET /v1/countries' before calling this endpoint.",
        security = [SecurityRequirement(name = "Bearer Authentication"), SecurityRequirement(name = "OAuth2 Scopes", scopes = ["SCOPE_breweries:write"])]
    )
    @ApiResponses(value = [
        ApiResponse(responseCode = "201", description = "Brewery created successfully"),
        ApiResponse(responseCode = "400", description = "Invalid input. Refer to 'remediation' and 'invalid-params' in the response for recovery steps."),
        ApiResponse(responseCode = "403", description = "Insufficient scope. Requires: breweries:write")
    ])
    @PreAuthorize("hasAuthority('${Scopes.BREWERY_WRITE}')")
    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE])
    fun register(@Valid @RequestBody inputBrewery: BreweryRegistrationDTO): ResponseEntity<ApiIndividualResponseDTO<BreweryResponseDTO>>{
        val brewery = this.breweryService.register(inputBrewery.name, inputBrewery.countryId!!)
        val location = URI("/v1/breweries/${brewery.id}")
        return ResponseEntity.created(location)
            .header("Content-Location", location.toString())
            .body(this.breweryPresenter.prepareRegisterBrewery(brewery))
    }

    @Operation(
        summary = "Get all breweries",
        description = "Retrieves all breweries. Use this to discover valid 'brewery_id' values before performing beer registrations.",
        security = [SecurityRequirement(name = "Bearer Authentication"), SecurityRequirement(name = "OAuth2 Scopes", scopes = ["SCOPE_breweries:read"])]
    )
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Successfully retrieved list"),
        ApiResponse(responseCode = "403", description = "Insufficient scope. Requires: breweries:read")
    ])
    @PreAuthorize("hasAuthority('${Scopes.BREWERY_READ}')")
    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE])
    fun getBreweries(): ResponseEntity<ApiCollectionResponseDTO<List<ApiIndividualResponseDTO<BreweryResponseDTO>>>>{
        val breweries = breweryService.getBreweries()
        return ResponseEntity.ok(this.breweryPresenter.prepareGetAllBreweries(breweries))
    }

    @Operation(
        summary = "Get a specific brewery",
        description = "Retrieves details of a specific brewery by ID.",
        security = [SecurityRequirement(name = "Bearer Authentication"), SecurityRequirement(name = "OAuth2 Scopes", scopes = ["SCOPE_breweries:read"])]
    )
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Successfully retrieved brewery"),
        ApiResponse(responseCode = "403", description = "Insufficient scope. Requires: breweries:read"),
        ApiResponse(responseCode = "404", description = "Brewery not found")
    ])
    @PreAuthorize("hasAuthority('${Scopes.BREWERY_READ}')")
    @GetMapping("/{breweryId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getSpecificBrewery(
        @Parameter(description = "ID of the brewery to be retrieved", example = "1")
        @PathVariable breweryId: Long
    ): ResponseEntity<ApiIndividualResponseDTO<BreweryResponseDTO>>{
        val brewery = this.breweryService.getSpecificBrewery(breweryId)
        return ResponseEntity.ok(this.breweryPresenter.prepareGetBrewery(brewery))
    }

    @Operation(
        summary = "Update a brewery",
        description = "Updates an existing brewery. You can change the name or country. Verify the country ID exists via 'GET /v1/countries' if updating the location.",
        security = [SecurityRequirement(name = "Bearer Authentication"), SecurityRequirement(name = "OAuth2 Scopes", scopes = ["SCOPE_breweries:write"])]
    )
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Brewery updated successfully"),
        ApiResponse(responseCode = "403", description = "Insufficient scope. Requires: breweries:write"),
        ApiResponse(responseCode = "404", description = "Brewery not found")
    ])
    @PreAuthorize("hasAuthority('${Scopes.BREWERY_WRITE}')")
    @PutMapping("/{breweryId}", consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun updateCountry(
        @Parameter(description = "ID of the brewery to be updated", example = "1")
        @PathVariable breweryId: Long,
        @RequestBody brewery: BreweryUpdateRequestDTO
    ): ResponseEntity<ApiIndividualResponseDTO<BreweryResponseDTO>>{
        val updatedBrewery = this.breweryService.updateBrewery(breweryId, brewery.name, brewery.countryId)
        return ResponseEntity.ok(this.breweryPresenter.prepareUpdateBrewery(updatedBrewery))
    }

    @Operation(
        summary = "Delete a brewery",
        description = "Permanently removes a brewery. This action is irreversible.",
        security = [SecurityRequirement(name = "Bearer Authentication"), SecurityRequirement(name = "OAuth2 Scopes", scopes = ["SCOPE_breweries:write"])]
    )
    @ApiResponses(value = [
        ApiResponse(responseCode = "204", description = "Successfully deleted"),
        ApiResponse(responseCode = "403", description = "Insufficient scope. Requires: breweries:write"),
        ApiResponse(responseCode = "404", description = "Brewery not found")
    ])
    @PreAuthorize("hasAuthority('${Scopes.BREWERY_WRITE}')")
    @DeleteMapping("/{breweryId}")
    fun deleteBrewery(
        @Parameter(description = "ID of the brewery to be deleted", example = "1")
        @PathVariable breweryId: Long
    ): ResponseEntity<Void>{
        this.breweryService.deleteBrewery(breweryId)
        return ResponseEntity.noContent().build()
    }
}