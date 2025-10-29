package com.biere.catalog.core.services

import com.biere.catalog.containers.api.dtos.BeerRegistrationDTO
import com.biere.catalog.containers.api.dtos.BeerResponseDTO
import com.biere.catalog.containers.api.dtos.BeerUpdateRequestDTO
import com.biere.catalog.core.exceptions.NotFoundException
import com.biere.catalog.adapters.entities.BeerEntity
import com.biere.catalog.adapters.repositories.BeerRepository
import com.biere.catalog.adapters.repositories.BreweryRepository
import com.biere.catalog.adapters.repositories.CountryRepository
import com.biere.catalog.adapters.repositories.StyleRepository
import com.opencsv.CSVWriter
import com.opencsv.bean.StatefulBeanToCsvBuilder
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.io.StringWriter
import java.nio.charset.StandardCharsets

@Service
class BeerService(
    private val beerRepository: BeerRepository,
    private val countryRepository: CountryRepository,
    private val breweryRepository: BreweryRepository,
    private val styleRepository: StyleRepository) {
    fun register(inputBeer: BeerRegistrationDTO): BeerResponseDTO {
        val brewery = breweryRepository.findById(inputBeer.breweryId).get()
        val style = styleRepository.findById(inputBeer.styleId).get()
        val beer = BeerEntity(name = inputBeer.name, alcoholContent = inputBeer.alcoholContent, brewery = brewery, style = style, year = inputBeer.year)
        val savedBeer = this.beerRepository.save(beer)
        return BeerResponseDTO(
            id = savedBeer.id ?: 0,
            name = savedBeer.name,
            countryName = savedBeer.brewery.country.name,
            alcoholContent = savedBeer.alcoholContent,
            brewery = savedBeer.brewery.name,
            style = savedBeer.style.name,
            year = savedBeer.year)
    }

    fun getBeers(): List<BeerResponseDTO>{
        val beers = beerRepository.findAll()
        return beers.stream().map { beer -> BeerResponseDTO(
            id = beer.id ?: 0,
            name = beer.name,
            countryName = beer.brewery.country.name,
            alcoholContent = beer.alcoholContent,
            brewery = beer.brewery.name,
            style = beer.style.name,
            year = beer.year) }.toList()
    }

    fun generateCsv(beers: List<BeerResponseDTO>): ByteArray {
        val writer = StringWriter()
        val beanToCsv = StatefulBeanToCsvBuilder<BeerResponseDTO>(writer)
            .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
            .withApplyQuotesToAll(false)
            .build()
        beanToCsv.write(beers)
        return writer.toString().toByteArray(StandardCharsets.UTF_8)
    }

    fun getSpecificBeer(beerId: Long): BeerResponseDTO {
        val optionalBeer = this.beerRepository.findById(beerId)
        if(optionalBeer.isEmpty){
            throw NotFoundException("Beer not found.")
        }
        val beer = optionalBeer.get()
        return BeerResponseDTO(
            id = beer.id ?: 0,
            name = beer.name,
            countryName = beer.brewery.country.name,
            alcoholContent = beer.alcoholContent,
            brewery = beer.brewery.name,
            style = beer.style.name,
            year = beer.year)
    }

    fun updateBeer(beerId: Long, beerUpdate: BeerUpdateRequestDTO): BeerResponseDTO{
        val country = this.countryRepository.findById(beerUpdate.countryId)
        val beer = this.beerRepository.findById(beerId).get()
        beer.brewery.country = country.get()
        this.beerRepository.save(beer)
        return BeerResponseDTO(
            id = beer.id ?: 0,
            name = beer.name,
            countryName = beer.brewery.country.name,
            alcoholContent = beer.alcoholContent,
            brewery = beer.brewery.name,
            style = beer.style.name,
            year = beer.year)
    }
}