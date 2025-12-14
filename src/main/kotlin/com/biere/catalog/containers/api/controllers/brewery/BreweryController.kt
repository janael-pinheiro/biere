package com.biere.catalog.containers.api.controllers.brewery

import com.biere.catalog.containers.api.dtos.ApiCollectionResponseDTO
import com.biere.catalog.containers.api.dtos.ApiGeneralRegistrationMetadataDTO
import com.biere.catalog.containers.api.dtos.ApiGeneralRegistrationOperationsDTO
import com.biere.catalog.containers.api.dtos.ApiGeneralRegistrationResponseDTO
import com.biere.catalog.containers.api.dtos.BreweryRegistrationDTO
import com.biere.catalog.containers.api.dtos.BreweryResponseDTO
import com.biere.catalog.containers.api.dtos.BreweryUpdateRequestDTO
import com.biere.catalog.core.services.BreweryService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping("/v1/breweries")
class BreweryController(private val breweryService: BreweryService) {
    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun register(@RequestBody inputBrewery: BreweryRegistrationDTO): ResponseEntity<ApiGeneralRegistrationResponseDTO<BreweryResponseDTO>>{
        val brewery = this.breweryService.register(inputBrewery)
        return ResponseEntity.created(URI("")).body(ApiGeneralRegistrationResponseDTO(data = brewery, metadata = ApiGeneralRegistrationMetadataDTO(
            ApiGeneralRegistrationOperationsDTO(null, null, null))))
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