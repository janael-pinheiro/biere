package com.biere.catalog.containers.api.controllers.beer

import com.biere.catalog.adapters.output.repositories.mappers.BeerMapper
import com.biere.catalog.containers.api.dtos.*
import com.biere.catalog.core.boundaries.output.BeerPresenterPort
import com.biere.catalog.core.models.PageRequest
import com.biere.catalog.core.models.UpdateBeerModel
import com.biere.catalog.core.services.BeerService
import org.springframework.core.io.ByteArrayResource
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.net.URI
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.Parameter
import jakarta.validation.Valid

import org.springframework.hateoas.MediaTypes
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

@RestController
@RequestMapping("/v1/beers")
@Tag(name = "Beers", description = "Beer management APIs")
class BeerController(private val beerService: BeerService, private val beerPresenter: BeerPresenterPort){
    @Operation(summary = "Register a new beer", description = "Creates a new beer record.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "201", description = "Beer created successfully"),
        ApiResponse(responseCode = "400", description = "Invalid input")
    ])
    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE, "application/hal+json"])
    fun register(@Valid @RequestBody beerRegistrationDTO: BeerRegistrationDTO): ResponseEntity<ApiIndividualResponseDTO<BeerResponseDTO>> {
        val beer = this.beerService.register(BeerMapper.mapToInputBeer(beerRegistrationDTO))
        val response = beerPresenter.prepareRegistrationResponse(beer)
        return ResponseEntity.created(URI("")).body(response)
    }


    @Operation(summary = "Get all beers (JSON)", description = "Retrieves a paginated list of beers in JSON format.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Successfully retrieved list")
    ])
    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE])
    fun getBeersJson(
        @Parameter(description = "Pagination information")
        @PageableDefault(page = 0, size = 10, sort = ["name"]) page: Pageable?
    ): ResponseEntity<ApiCollectionResponseDTO<List<ApiIndividualResponseDTO<BeerResponseDTO>>>>{
        val beers = this.beerService.getBeers(PageRequest(number = page!!.pageNumber, size = page.pageSize, sort = page.sort.getOrderFor("name")?.property.toString()))
        val uri = ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString()
        return ResponseEntity.ok(beerPresenter.prepareJsonData(page, beers, uri))
    }

    @Operation(summary = "Get all beers (CSV)", description = "Retrieves a list of beers in CSV format.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Successfully retrieved CSV file")
    ])
    @GetMapping(produces = ["text/csv"])
    fun getBeersCsv(
        @Parameter(description = "Pagination information")
        @PageableDefault(page = 0, size = 100, sort = ["name"]) page: Pageable
    ): ResponseEntity<ByteArrayResource>{
        val beers = this.beerService.getBeers(PageRequest(number = page.pageNumber, size = page.pageSize, sort = page.sort.getOrderFor("name")?.property.toString()))
        return ResponseEntity
            .ok()
            .header("Content-Disposition", "attachment; filename=\"beers.csv\"")
            .contentType(MediaType.parseMediaType("text/csv"))
            .body(beerPresenter.prepareCsvData(beers))
    }

    @Operation(summary = "Get a specific beer", description = "Retrieves details of a specific beer by ID.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Successfully retrieved beer"),
        ApiResponse(responseCode = "404", description = "Beer not found")
    ])
    @GetMapping("/{beerId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getSpecificBeer(
        @Parameter(description = "ID of the beer to be retrieved", example = "1")
        @PathVariable beerId: Long
    ): ResponseEntity<ApiIndividualResponseDTO<BeerResponseDTO>>{
        val beer = this.beerService.getSpecificBeer(beerId)
        return ResponseEntity.ok(beerPresenter.prepareGetBeer(beer))
    }

    @Operation(summary = "Update a beer", description = "Updates an existing beer by ID.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Beer updated successfully"),
        ApiResponse(responseCode = "404", description = "Beer not found")
    ])
    @PatchMapping("/{beerId}", consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun updateBeer(
        @Parameter(description = "ID of the beer to be updated", example = "1")
        @PathVariable beerId: Long,
        @RequestBody beerUpdateRequest: BeerUpdateRequestDTO
    ): ResponseEntity<ApiIndividualResponseDTO<BeerResponseDTO>>{
        val beer = this.beerService.updateBeer(beerId, UpdateBeerModel(
            name = beerUpdateRequest.name,
            alcoholContent = beerUpdateRequest.alcoholContent,
            breweryId = beerUpdateRequest.breweryId,
            styleId = beerUpdateRequest.styleId,
            year = beerUpdateRequest.year))
        return ResponseEntity.ok().body(beerPresenter.prepareUpdateBrewery(beer))
    }

    @Operation(summary = "Delete a beer", description = "Deletes a beer by ID.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "204", description = "Beer deleted successfully"),
        ApiResponse(responseCode = "404", description = "Beer not found")
    ])
    @DeleteMapping("/{beerId}")
    fun deleteBeer(
        @Parameter(description = "ID of the beer to be deleted", example = "1")
        @PathVariable beerId: Long
    ): ResponseEntity<Void> {
        beerService.deleteBeer(beerId)
        return ResponseEntity.noContent().build()
    }

}