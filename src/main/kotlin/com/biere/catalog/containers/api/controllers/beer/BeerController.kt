package com.biere.catalog.containers.api.controllers.beer

import com.biere.catalog.containers.api.dtos.ApiGeneralRegistrationResponseDTO
import com.biere.catalog.containers.api.dtos.BeerRegistrationDTO
import com.biere.catalog.containers.api.dtos.BeerResponseDTO
import com.biere.catalog.containers.api.dtos.BeerUpdateRequestDTO
import com.biere.catalog.core.services.BeerService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/v1/beers")
class BeerController(private val beerService: BeerService){
    @PostMapping
    fun register(@RequestBody beerRegistrationDTO: BeerRegistrationDTO): ResponseEntity<ApiGeneralRegistrationResponseDTO<BeerResponseDTO>> {
        val beer = this.beerService.register(beerRegistrationDTO)
        val links = listOf(
            "GET /v1/beers/${beer.id}",
            "PATCH /v1/beers/${beer.id}")
        val response = ApiGeneralRegistrationResponseDTO(data = beer, links = links)
        return ResponseEntity.created(URI("")).body(response)
    }

    @GetMapping("/{beerId}")
    fun getSpecificBeer(@PathVariable beerId: Long): ResponseEntity<BeerResponseDTO>{
        val beerResponse = this.beerService.getSpecificBeer(beerId)
        return ResponseEntity.ok(beerResponse)
    }

    @PatchMapping("/{beerId}")
    fun updateBeer(@PathVariable beerId: Long, @RequestBody beerUpdateRequest: BeerUpdateRequestDTO): ResponseEntity<BeerResponseDTO>{
        val beer = this.beerService.updateBeer(beerId, beerUpdateRequest)
        return ResponseEntity.ok().body(beer)
    }

}