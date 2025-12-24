package com.biere.catalog.containers.api.controllers.beer

import com.biere.catalog.adapters.output.repositories.mappers.BeerMapper
import com.biere.catalog.containers.api.dtos.*
import com.biere.catalog.core.boundaries.output.BeerPresenterOutputPort
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

@RestController
@RequestMapping("/v1/beers")
class BeerController(private val beerService: BeerService, private val beerPresenter: BeerPresenterOutputPort){
    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun register(@Validated @RequestBody beerRegistrationDTO: BeerRegistrationDTO): ResponseEntity<ApiGeneralRegistrationResponseDTO<BeerResponseDTO>> {
        val beer = this.beerService.register(BeerMapper.mapToInputBeer(beerRegistrationDTO))
        val response = beerPresenter.prepareRegistrationResponse(beer)
        return ResponseEntity.created(URI("")).body(response)
    }

    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getBeersJson(@PageableDefault(page = 0, size = 10, sort = ["name"]) page: Pageable): ResponseEntity<ApiCollectionResponseDTO<List<BeerResponseDTO>>>{
        val beers = this.beerService.getBeers(PageRequest(number = page.pageNumber, size = page.pageSize, sort = page.sort.getOrderFor("name")?.property.toString()))
        return ResponseEntity.ok(beerPresenter.prepareJsonData(page, beers))
    }

    @GetMapping(produces = ["text/csv"])
    fun getBeersCsv(@PageableDefault(page = 0, size = 100, sort = ["name"]) page: Pageable): ResponseEntity<ByteArrayResource>{
        val beers = this.beerService.getBeers(PageRequest(number = page.pageNumber, size = page.pageSize, sort = page.sort.getOrderFor("name")?.property.toString()))
        return ResponseEntity
            .ok()
            .header("Content-Disposition", "attachment; filename=\"beers.csv\"")
            .contentType(MediaType.parseMediaType("text/csv"))
            .body(beerPresenter.prepareCsvData(beers))
    }

    @GetMapping("/{beerId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getSpecificBeer(@PathVariable beerId: Long): ResponseEntity<BeerResponseDTO>{
        val beer = this.beerService.getSpecificBeer(beerId)
        return ResponseEntity.ok(BeerMapper.mapToBeerResponseDTO(beer))
    }

    @PatchMapping("/{beerId}", consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun updateBeer(@PathVariable beerId: Long, @RequestBody beerUpdateRequest: BeerUpdateRequestDTO): ResponseEntity<BeerResponseDTO>{
        val beer = this.beerService.updateBeer(beerId, UpdateBeerModel(
            name = beerUpdateRequest.name,
            alcoholContent = beerUpdateRequest.alcoholContent,
            breweryId = beerUpdateRequest.breweryId,
            styleId = beerUpdateRequest.styleId,
            year = beerUpdateRequest.year))
        return ResponseEntity.ok().body(BeerMapper.mapToBeerResponseDTO(beer))
    }

    @DeleteMapping("/{beerId}")
    fun deleteBeer(@PathVariable beerId: Long): ResponseEntity<Void> {
        beerService.deleteBeer(beerId)
        return ResponseEntity.noContent().build()
    }

}