package com.biere.catalog.core.services

import com.biere.catalog.core.dto.BeerRegistrationDTO
import com.biere.catalog.core.dto.BeerResponseDTO
import com.biere.catalog.core.dto.BeerUpdateRequestDTO
import com.biere.catalog.core.exceptions.NotFoundException
import com.biere.catalog.infrastructure.entities.BeerEntity
import com.biere.catalog.infrastructure.repositories.BeerRepository
import com.biere.catalog.infrastructure.repositories.BreweryRepository
import com.biere.catalog.infrastructure.repositories.CountryRepository
import org.springframework.stereotype.Service

@Service
class BeerService(private val beerRepository: BeerRepository, private val countryRepository: CountryRepository, val breweryRepository: BreweryRepository) {
    fun register(inputBeer: BeerRegistrationDTO): Long? {
        val brewery = breweryRepository.findById(inputBeer.breweryId).get()
        val beer = BeerEntity(name = inputBeer.name, alcoholContent = inputBeer.alcoholContent, brewery = brewery)
        val savedBeer = this.beerRepository.save(beer)
        return savedBeer.id
    }

    fun getSpecificBeer(beerId: Long): BeerResponseDTO {
        val optionalBeer = this.beerRepository.findById(beerId)
        if(optionalBeer.isEmpty){
            throw NotFoundException("Beer not found.")
        }
        val beer = optionalBeer.get()
        return BeerResponseDTO(
            name = beer.name,
            countryName = beer.brewery.country.name,
            alcoholContent = beer.alcoholContent,
            brewery = beer.brewery.name)
    }

    fun updateBeer(beerId: Long, beerUpdate: BeerUpdateRequestDTO): BeerResponseDTO{
        val country = this.countryRepository.findById(beerUpdate.countryId)
        val beer = this.beerRepository.findById(beerId).get()
        beer.brewery.country = country.get()
        this.beerRepository.save(beer)
        return BeerResponseDTO(name = beer.name, countryName = beer.brewery.country.name, alcoholContent = beer.alcoholContent, brewery = beer.brewery.name)

    }
}