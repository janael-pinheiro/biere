package com.biere.catalog.containers.api.controllers.beer

import com.biere.catalog.containers.api.dtos.ApiCollectionResponseDTO
import com.biere.catalog.containers.api.dtos.ApiGeneralRegistrationResponseDTO
import com.biere.catalog.containers.api.dtos.BeerRegistrationDTO
import com.biere.catalog.containers.api.dtos.BeerResponseDTO
import com.biere.catalog.containers.api.dtos.BeerUpdateRequestDTO
import com.biere.catalog.core.services.BeerService
import com.opencsv.CSVWriter
import com.opencsv.bean.StatefulBeanToCsv
import com.opencsv.bean.StatefulBeanToCsvBuilder
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.Resource
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.io.OutputStream
import java.io.StringWriter
import java.net.URI
import java.nio.charset.StandardCharsets

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

    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getBeersJson(): ResponseEntity<ApiCollectionResponseDTO<List<BeerResponseDTO>>>{
        val beers = this.beerService.getBeers()
        return ResponseEntity.ok(ApiCollectionResponseDTO(data = beers))
    }

    @GetMapping(produces = ["text/csv"])
    fun getBeersCsv(): ResponseEntity<ByteArrayResource>{
        val beers = this.beerService.getBeers()
        val output = this.beerService.generateCsv(beers)
        val resource = ByteArrayResource(output)
        return ResponseEntity
            .ok()
            .header("Content-Disposition", "attachment; filename=\"beers.csv\"")
            .contentType(MediaType.parseMediaType("text/csv"))
            .body(resource)
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