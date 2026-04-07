package com.biere.catalog.infrastructure.adapter.input.rest.controllers.beer

import com.biere.catalog.infrastructure.adapter.input.rest.mappers.BeerMapper
import com.biere.catalog.infrastructure.adapter.input.rest.dtos.*
import com.biere.catalog.domain.port.output.BeerPresenterPort
import com.biere.catalog.domain.model.PageRequest
import com.biere.catalog.domain.model.UpdateBeerModel
import com.biere.catalog.domain.port.input.BeerUseCase
import org.springframework.core.io.ByteArrayResource
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.security.access.prepost.PreAuthorize
import java.net.URI
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid

import org.springframework.hateoas.MediaTypes
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import com.biere.catalog.infrastructure.adapter.input.rest.controllers.Scopes
import com.biere.catalog.infrastructure.configuration.annotations.DeprecatedEndpoint

@RestController
@RequestMapping("/v1/beers")
@Tag(name = "Beers", description = "Beer management APIs")
@SecurityRequirement(name = "Bearer Authentication")
@SecurityRequirement(name = "Idempotency Key")
class BeerController(private val beerService: BeerUseCase, private val beerPresenter: BeerPresenterPort){
    @Operation(
        summary = "Register a new beer",
        description = "Creates a new beer record. Before calling this, ensure you have valid 'brewery_id' and 'style_id'. Obtain them from 'GET /v1/breweries' and 'GET /v1/styles' respectively. You MUST include a unique 'X-Idempotency-Key' header.",
        security = [SecurityRequirement(name = "Bearer Authentication"), SecurityRequirement(name = "OAuth2 Scopes", scopes = [Scopes.BEER_WRITE])]
    )
    @ApiResponses(value = [
        ApiResponse(responseCode = "201", description = "Beer created successfully"),
        ApiResponse(responseCode = "400", description = "Invalid input. Check 'invalid-params' in the response for prescriptive correction."),
        ApiResponse(responseCode = "403", description = "Insufficient scope. Requires: beers:write")
    ])
    @PreAuthorize("hasAuthority('${Scopes.BEER_WRITE}')")
    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE, "application/hal+json"])
    fun register(@Valid @RequestBody beerRegistrationDTO: BeerRegistrationDTO): ResponseEntity<ApiIndividualResponseDTO<BeerResponseDTO>> {
        val beer = this.beerService.register(BeerMapper.mapToInputBeer(beerRegistrationDTO))
        val response = beerPresenter.prepareRegistrationResponse(beer)
        val location = URI("/v1/beers/${beer.id}")
        return ResponseEntity.created(location)
            .header("Content-Location", location.toString())
            .body(response)
    }


    @Operation(
        summary = "Get all beers (JSON)",
        description = "Retrieves a paginated list of beers. For autonomous navigation, prefer using the HATEOAS links in the 'page' metadata (first, last, next, previous) instead of building URLs manually.",
        security = [SecurityRequirement(name = "Bearer Authentication"), SecurityRequirement(name = "OAuth2 Scopes", scopes = [Scopes.BEER_READ])]
    )
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Successfully retrieved list"),
        ApiResponse(responseCode = "403", description = "Insufficient scope. Requires: beers:read")
    ])
    @PreAuthorize("hasAuthority('${Scopes.BEER_READ}')")
    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE])
    fun getBeersJson(
        @Parameter(description = "Pagination information")
        @PageableDefault(page = 0, size = 10, sort = ["name"]) page: Pageable?
    ): ResponseEntity<ApiCollectionResponseDTO<List<ApiIndividualResponseDTO<BeerResponseDTO>>>>{
        val beers = this.beerService.getBeers(PageRequest(number = page!!.pageNumber, size = page.pageSize, sort = page.sort.getOrderFor("name")?.property.toString()))
        val uri = ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString()
        return ResponseEntity.ok(beerPresenter.prepareJsonData(page, beers, uri))
    }

    @Operation(summary = "Get all beers (CSV)", description = "Retrieves a list of beers in CSV format. Note: This endpoint is deprecated and will be removed in the future. Use the JSON endpoint for full HATEOAS support.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Successfully retrieved CSV file")
    ])
    @DeprecatedEndpoint(
        sunset = "2026-12-31",
        successor = "/v1/beers"
    )
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

    @Operation(
        summary = "Get a specific beer",
        description = "Retrieves details of a specific beer. Use this to verify the current state before performing an update or delete operation.",
        security = [SecurityRequirement(name = "Bearer Authentication"), SecurityRequirement(name = "OAuth2 Scopes", scopes = [Scopes.BEER_READ])]
    )
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Successfully retrieved beer"),
        ApiResponse(responseCode = "403", description = "Insufficient scope. Requires: beers:read"),
        ApiResponse(responseCode = "404", description = "Beer not found. If this occurs during a loop, verify the ID from the collection list.")
    ])
    @PreAuthorize("hasAuthority('${Scopes.BEER_READ}')")
    @GetMapping("/{beerId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getSpecificBeer(
        @Parameter(description = "ID of the beer to be retrieved", example = "1")
        @PathVariable beerId: Long
    ): ResponseEntity<ApiIndividualResponseDTO<BeerResponseDTO>>{
        val beer = this.beerService.getSpecificBeer(beerId)
        return ResponseEntity.ok(beerPresenter.prepareGetBeer(beer))
    }

    @Operation(
        summary = "Update a beer",
        description = "Performs a partial update on an existing beer. Only send the fields that need to change. Ensure the beer exists by calling 'GET /v1/beers/{beerId}' first if the ID was not obtained recently.",
        security = [SecurityRequirement(name = "Bearer Authentication"), SecurityRequirement(name = "OAuth2 Scopes", scopes = [Scopes.BEER_WRITE])]
    )
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Beer updated successfully"),
        ApiResponse(responseCode = "403", description = "Insufficient scope. Requires: beers:write"),
        ApiResponse(responseCode = "404", description = "Beer not found. Check if the resource was deleted by another process.")
    ])
    @PreAuthorize("hasAuthority('${Scopes.BEER_WRITE}')")
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
        return ResponseEntity.ok().body(beerPresenter.prepareUpdateBeer(beer))
    }

    @Operation(
        summary = "Delete a beer",
        description = "Permanently removes a beer. This action is irreversible. Confirm the resource exists via 'GET /v1/beers/{beerId}' before deleting.",
        security = [SecurityRequirement(name = "Bearer Authentication"), SecurityRequirement(name = "OAuth2 Scopes", scopes = [Scopes.BEER_WRITE])]
    )
    @ApiResponses(value = [
        ApiResponse(responseCode = "204", description = "Beer deleted successfully"),
        ApiResponse(responseCode = "403", description = "Insufficient scope. Requires: beers:write"),
        ApiResponse(responseCode = "404", description = "Beer not found")
    ])
    @PreAuthorize("hasAuthority('${Scopes.BEER_WRITE}')")
    @DeleteMapping("/{beerId}")
    fun deleteBeer(
        @Parameter(description = "ID of the beer to be deleted", example = "1")
        @PathVariable beerId: Long
    ): ResponseEntity<Void> {
        beerService.deleteBeer(beerId)
        return ResponseEntity.noContent().build()
    }

}