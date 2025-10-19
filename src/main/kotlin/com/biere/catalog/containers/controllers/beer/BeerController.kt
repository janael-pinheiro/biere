package com.biere.catalog.containers.controllers.beer

import com.biere.catalog.core.dto.BeerRegistrationDTO
import com.biere.catalog.core.dto.BeerRegistrationResponseDTO
import com.biere.catalog.core.dto.BeerResponseDTO
import com.biere.catalog.core.dto.BeerUpdateRequestDTO
import com.biere.catalog.core.services.BeerService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping("/v1/beers")
class BeerController(val beerService: BeerService){
    @PostMapping
    fun register(@RequestBody beerRegistrationDTO: BeerRegistrationDTO): ResponseEntity<BeerRegistrationResponseDTO> {
        val beerId = this.beerService.register(beerRegistrationDTO)
        return ResponseEntity.created(URI("")).body(
            BeerRegistrationResponseDTO(listOf("GET /v1/beers/$beerId")))
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