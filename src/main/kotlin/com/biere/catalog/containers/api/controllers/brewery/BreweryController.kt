package com.biere.catalog.containers.api.controllers.brewery

import com.biere.catalog.containers.api.dtos.*
import com.biere.catalog.core.services.BreweryService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses

@RestController
@RequestMapping("/v1/breweries")
@Tag(name = "Breweries", description = "Brewery management APIs")
class BreweryController(private val breweryService: BreweryService) {
    @Operation(summary = "Register a new brewery", description = "Creates a new brewery.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "201", description = "Brewery created successfully"),
        ApiResponse(responseCode = "400", description = "Invalid input")
    ])
    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun register(@RequestBody inputBrewery: BreweryRegistrationDTO): ResponseEntity<ApiGeneralRegistrationResponseDTO<BreweryResponseDTO>>{
        val brewery = this.breweryService.register(inputBrewery)
        return ResponseEntity.created(URI("")).body(ApiGeneralRegistrationResponseDTO(data = brewery))
    }

    @Operation(summary = "Get all breweries", description = "Retrieves a list of all breweries.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Successfully retrieved list")
    ])
    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getBreweries(): ResponseEntity<ApiCollectionResponseDTO<List<BreweryResponseDTO>>>{
        val breweries = breweryService.getBreweries()
        return ResponseEntity.ok(ApiCollectionResponseDTO(data = breweries, page = null))
    }

    @Operation(summary = "Get a specific brewery", description = "Retrieves details of a specific brewery by ID.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Successfully retrieved brewery"),
        ApiResponse(responseCode = "404", description = "Brewery not found")
    ])
    @GetMapping("/{breweryId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getSpecificBrewery(@PathVariable breweryId: Long): ResponseEntity<BreweryResponseDTO>{
        val brewery = this.breweryService.getSpecificBrewery(breweryId)
        return ResponseEntity.ok(brewery)
    }

    @Operation(summary = "Update a brewery", description = "Updates an existing brewery by ID.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Brewery updated successfully"),
        ApiResponse(responseCode = "404", description = "Brewery not found")
    ])
    @PutMapping("/{breweryId}", consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun updateCountry(@PathVariable breweryId: Long, @RequestBody brewery: BreweryUpdateRequestDTO): ResponseEntity<BreweryResponseDTO>{
        val updatedBrewery = this.breweryService.updateBrewery(breweryId, brewery)
        return ResponseEntity.ok(updatedBrewery)
    }
}