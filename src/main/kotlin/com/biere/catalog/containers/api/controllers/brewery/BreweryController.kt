package com.biere.catalog.containers.api.controllers.brewery

import com.biere.catalog.containers.api.dtos.*
import com.biere.catalog.core.services.BreweryService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/v1/breweries")
class BreweryController(private val breweryService: BreweryService) {
    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun register(@RequestBody inputBrewery: BreweryRegistrationDTO): ResponseEntity<ApiGeneralRegistrationResponseDTO<BreweryResponseDTO>>{
        val brewery = this.breweryService.register(inputBrewery)
        return ResponseEntity.created(URI("")).body(ApiGeneralRegistrationResponseDTO(data = brewery))
    }

    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getBreweries(): ResponseEntity<ApiCollectionResponseDTO<List<BreweryResponseDTO>>>{
        val breweries = breweryService.getBreweries()
        return ResponseEntity.ok(ApiCollectionResponseDTO(data = breweries, page = null))
    }

    @GetMapping("/{breweryId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getSpecificBrewery(@PathVariable breweryId: Long): ResponseEntity<BreweryResponseDTO>{
        val brewery = this.breweryService.getSpecificBrewery(breweryId)
        return ResponseEntity.ok(brewery)
    }

    @PutMapping("/{breweryId}", consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun updateCountry(@PathVariable breweryId: Long, @RequestBody brewery: BreweryUpdateRequestDTO): ResponseEntity<BreweryResponseDTO>{
        val updatedBrewery = this.breweryService.updateBrewery(breweryId, brewery)
        return ResponseEntity.ok(updatedBrewery)
    }
}