package com.biere.catalog.core.services

import com.biere.catalog.core.dto.BreweryRegistrationDTO
import com.biere.catalog.core.dto.BreweryResponseDTO
import com.biere.catalog.core.exceptions.NotFoundException
import com.biere.catalog.adapters.entities.BreweryEntity
import com.biere.catalog.adapters.repositories.BreweryRepository
import com.biere.catalog.adapters.repositories.CountryRepository
import org.springframework.stereotype.Service

@Service
class BreweryService(private val breweryRepository: BreweryRepository, private val countryRepository: CountryRepository) {
    fun register(inputBrewery: BreweryRegistrationDTO): Long? {
        val country = countryRepository.findById(inputBrewery.countryId).get()
        val newBrewery = BreweryEntity(name = inputBrewery.name, country = country)
        val savedBrewery = breweryRepository.save(newBrewery)
        return savedBrewery.id
    }

    fun getSpecificBrewery(breweryId: Long): BreweryResponseDTO {
        val brewery = breweryRepository.findById(breweryId)
        if(brewery.isEmpty){
            throw NotFoundException("Brewery not found.")
        }
        return BreweryResponseDTO(id = brewery.get().id, name = brewery.get().name, countryName = brewery.get().country.name)
    }
}