package com.biere.catalog.containers.controllers.brewery

import com.biere.catalog.core.dto.BreweryRegistrationDTO
import com.biere.catalog.core.dto.BreweryRegistrationResponseDTO
import com.biere.catalog.core.dto.BreweryResponseDTO
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
    fun register(@RequestBody inputBrewery: BreweryRegistrationDTO): ResponseEntity<BreweryRegistrationResponseDTO>{
        val breweryId = this.breweryService.register(inputBrewery)
        return ResponseEntity.created(URI("")).body(BreweryRegistrationResponseDTO(listOf("GET /v1/breweries/$breweryId")))
    }

    @GetMapping("/{breweryId}")
    fun getSpecificBrewery(@PathVariable breweryId: Long): ResponseEntity<BreweryResponseDTO>{
        val brewery = this.breweryService.getSpecificBrewery(breweryId)
        return ResponseEntity.ok(brewery)
    }
}