package com.biere.catalog.containers.api.controllers.beer

import com.biere.catalog.containers.api.dtos.*
import com.biere.catalog.containers.api.presenters.BeerPresenter
import com.biere.catalog.core.services.BeerService
import org.springframework.core.io.ByteArrayResource
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/v1/beers")
class BeerController(private val beerService: BeerService){
    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun register(@RequestBody beerRegistrationDTO: BeerRegistrationDTO): ResponseEntity<ApiGeneralRegistrationResponseDTO<BeerResponseDTO>> {
        val beer = this.beerService.register(beerRegistrationDTO)
        val self = ApiGeneralRegistrationMetadataDTOFactory.createAction("/v1/beers/${beer.id}", "GET", "application/json")
        val update = ApiGeneralRegistrationMetadataDTOFactory.createAction("/v1/beers/${beer.id}", "PATCH", "application/json")
        val operations = ApiGeneralRegistrationMetadataDTOFactory.createOperations(self = self, update = update, delete = null)
        val metadata = ApiGeneralRegistrationMetadataDTOFactory.create(links = operations)
        val response = ApiGeneralRegistrationResponseDTO(data = beer, metadata = metadata)
        return ResponseEntity.created(URI("")).body(response)
    }

    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getBeersJson(@PageableDefault(page = 0, size = 1, sort = ["name"]) page: Pageable): ResponseEntity<ApiCollectionResponseDTO<List<BeerResponseDTO>>>{
        val beers = this.beerService.getBeers(page)
        return ResponseEntity.ok(BeerPresenter().prepareJsonData(page, beers))
    }

    @GetMapping(produces = ["text/csv"])
    fun getBeersCsv(@PageableDefault(page = 0, size = 1, sort = ["name"]) page: Pageable): ResponseEntity<ByteArrayResource>{
        val beers = this.beerService.getBeers(page)
        return ResponseEntity
            .ok()
            .header("Content-Disposition", "attachment; filename=\"beers.csv\"")
            .contentType(MediaType.parseMediaType("text/csv"))
            .body(BeerPresenter().prepareCsvData(beers))
    }

    @GetMapping("/{beerId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getSpecificBeer(@PathVariable beerId: Long): ResponseEntity<BeerResponseDTO>{
        val beerResponse = this.beerService.getSpecificBeer(beerId)
        return ResponseEntity.ok(beerResponse)
    }

    @PatchMapping("/{beerId}", consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun updateBeer(@PathVariable beerId: Long, @RequestBody beerUpdateRequest: BeerUpdateRequestDTO): ResponseEntity<BeerResponseDTO>{
        val beer = this.beerService.updateBeer(beerId, beerUpdateRequest)
        return ResponseEntity.ok().body(beer)
    }

}