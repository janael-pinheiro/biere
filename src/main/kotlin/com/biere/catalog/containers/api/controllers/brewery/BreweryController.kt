package com.biere.catalog.containers.api.controllers.brewery

import com.biere.catalog.containers.api.dtos.ApiGeneralRegistrationResponseDTO
import com.biere.catalog.containers.api.dtos.BreweryRegistrationDTO
import com.biere.catalog.containers.api.dtos.BreweryResponseDTO
import com.biere.catalog.core.services.BreweryService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping("/v1/breweries")
class BreweryController(private val breweryService: BreweryService) {
    @PostMapping
    fun register(@RequestBody inputBrewery: BreweryRegistrationDTO): ResponseEntity<ApiGeneralRegistrationResponseDTO<BreweryResponseDTO>>{
        val brewery = this.breweryService.register(inputBrewery)
        val links = listOf("GET /v1/breweries/${brewery.id}")
        return ResponseEntity.created(URI("")).body(ApiGeneralRegistrationResponseDTO(data = brewery, links = links))
    }

    @GetMapping("/{breweryId}")
    fun getSpecificBrewery(@PathVariable breweryId: Long): ResponseEntity<BreweryResponseDTO>{
        val brewery = this.breweryService.getSpecificBrewery(breweryId)
        return ResponseEntity.ok(brewery)
    }
}