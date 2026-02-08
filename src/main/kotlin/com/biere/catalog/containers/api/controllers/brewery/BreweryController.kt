package com.biere.catalog.containers.api.controllers.brewery

import com.biere.catalog.containers.api.dtos.*
import com.biere.catalog.containers.api.presenters.BreweryPresenterAdapter
import com.biere.catalog.core.exceptions.NotFoundException
import com.biere.catalog.core.services.BreweryService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import jakarta.validation.Valid
import org.springframework.hateoas.MediaTypes

@RestController
@RequestMapping("/v1/breweries")
@Tag(name = "Breweries", description = "Brewery management APIs")
class BreweryController(private val breweryService: BreweryService, private val breweryPresenter: BreweryPresenterAdapter = BreweryPresenterAdapter()) {
    @Operation(summary = "Register a new brewery", description = "Creates a new brewery.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "201", description = "Brewery created successfully"),
        ApiResponse(responseCode = "400", description = "Invalid input")
    ])
    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE])
    fun register(@Valid @RequestBody inputBrewery: BreweryRegistrationDTO): ResponseEntity<ApiIndividualResponseDTO<BreweryResponseDTO>>{
        val brewery = this.breweryService.register(inputBrewery)
        return ResponseEntity.created(URI("/v1/breweries/${brewery.id}")).body(this.breweryPresenter.prepareRegisterBrewery(brewery))
    }

    @Operation(summary = "Get all breweries", description = "Retrieves a list of all breweries.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Successfully retrieved list")
    ])
    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE])
    fun getBreweries(): ResponseEntity<ApiCollectionResponseDTO<List<ApiIndividualResponseDTO<BreweryResponseDTO>>>>{
        val breweries = breweryService.getBreweries()
        return ResponseEntity.ok(this.breweryPresenter.prepareGetAllBreweries(breweries))
    }

    @Operation(summary = "Get a specific brewery", description = "Retrieves details of a specific brewery by ID.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Successfully retrieved brewery"),
        ApiResponse(responseCode = "404", description = "Brewery not found")
    ])
    @GetMapping("/{breweryId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getSpecificBrewery(
        @Parameter(description = "ID of the brewery to be retrieved", example = "1")
        @PathVariable breweryId: Long
    ): ResponseEntity<ApiIndividualResponseDTO<BreweryResponseDTO>>{
        val brewery = this.breweryService.getSpecificBrewery(breweryId)
        return ResponseEntity.ok(this.breweryPresenter.prepareGetBrewery(brewery))
    }

    @Operation(summary = "Update a brewery", description = "Updates an existing brewery by ID.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Brewery updated successfully"),
        ApiResponse(responseCode = "404", description = "Brewery not found")
    ])
    @PutMapping("/{breweryId}", consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun updateCountry(
        @Parameter(description = "ID of the brewery to be updated", example = "1")
        @PathVariable breweryId: Long,
        @RequestBody brewery: BreweryUpdateRequestDTO
    ): ResponseEntity<ApiIndividualResponseDTO<BreweryResponseDTO>>{
        val updatedBrewery = this.breweryService.updateBrewery(breweryId, brewery)
        return ResponseEntity.ok(this.breweryPresenter.prepareUpdateBrewery(updatedBrewery))
    }

    @Operation(summary = "Delete a brewery", description = "Deletes an existing brewery based on the ID.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Successfully deleted"),
        ApiResponse(responseCode = "404", description = "Brewery not found")
    ])
    @DeleteMapping("/{breweryId}")
    fun deleteBrewery(
        @Parameter(description = "ID of the brewery to be deleted", example = "1")
        @PathVariable breweryId: Long
    ): ResponseEntity<Void>{
        this.breweryService.deleteBrewery(breweryId)
        return ResponseEntity.noContent().build()
    }
}