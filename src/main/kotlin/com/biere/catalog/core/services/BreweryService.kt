package com.biere.catalog.core.services

import com.biere.catalog.containers.api.dtos.BreweryRegistrationDTO
import com.biere.catalog.containers.api.dtos.BreweryResponseDTO
import com.biere.catalog.core.exceptions.NotFoundException
import com.biere.catalog.adapters.entities.BreweryEntity
import com.biere.catalog.adapters.output.repositories.BreweryRepository
import com.biere.catalog.adapters.output.repositories.CountryRepository
import org.springframework.stereotype.Service

@Service
class BreweryService(private val breweryRepository: BreweryRepository, private val countryRepository: CountryRepository) {
    fun register(inputBrewery: BreweryRegistrationDTO): BreweryResponseDTO {
        val country = countryRepository.findById(inputBrewery.countryId).get()
        val newBrewery = BreweryEntity(name = inputBrewery.name, country = country)
        val savedBrewery = breweryRepository.save(newBrewery)
        return BreweryResponseDTO(id = savedBrewery.id ?: 0, name = savedBrewery.name, countryName = savedBrewery.country.name)
    }

    fun getSpecificBrewery(breweryId: Long): BreweryResponseDTO {
        val brewery = breweryRepository.findById(breweryId)
        if(brewery.isEmpty){
            throw NotFoundException("Brewery not found.")
        }
        return BreweryResponseDTO(id = brewery.get().id, name = brewery.get().name, countryName = brewery.get().country.name)
    }
}