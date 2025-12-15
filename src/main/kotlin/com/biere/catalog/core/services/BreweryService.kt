package com.biere.catalog.core.services

import com.biere.catalog.containers.api.dtos.BreweryRegistrationDTO
import com.biere.catalog.containers.api.dtos.BreweryResponseDTO
import com.biere.catalog.core.exceptions.NotFoundException
import com.biere.catalog.adapters.entities.BreweryEntity
import com.biere.catalog.adapters.output.repositories.BreweryRepository
import com.biere.catalog.adapters.output.repositories.CountryRepository
import com.biere.catalog.containers.api.dtos.BreweryUpdateRequestDTO
import com.biere.catalog.core.exceptions.ConflictException
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class BreweryService(private val breweryRepository: BreweryRepository, private val countryRepository: CountryRepository) {
    fun register(inputBrewery: BreweryRegistrationDTO): BreweryResponseDTO {
        if (breweryRepository.existsByName(inputBrewery.name)){
            throw ConflictException("The name ${inputBrewery.name} already exists.")
        }
        val country = countryRepository.findById(inputBrewery.countryId).get()
        val newBrewery = BreweryEntity(name = inputBrewery.name, country = country)
        val savedBrewery = breweryRepository.save(newBrewery)
        return BreweryResponseDTO(id = savedBrewery.id ?: 0, name = savedBrewery.name, countryName = savedBrewery.country.name)
    }

    fun getBreweries(): List<BreweryResponseDTO> {
        return this.breweryRepository.findAll().stream().map { brewery -> BreweryResponseDTO(
            id = brewery.id ?: 0,
            name = brewery.name,
            countryName = brewery.country.name) }.toList()
    }

    fun getSpecificBrewery(breweryId: Long): BreweryResponseDTO {
        val brewery = breweryRepository.findById(breweryId)
        if(brewery.isEmpty){
            throw NotFoundException("Brewery not found.")
        }
        return BreweryResponseDTO(id = brewery.get().id, name = brewery.get().name, countryName = brewery.get().country.name)
    }

    fun updateBrewery(breweryId: Long, inputBrewery: BreweryUpdateRequestDTO): BreweryResponseDTO {
        val brewery = breweryRepository.findById(breweryId).orElseThrow { NotFoundException("Brewery not found.") }
        val country = countryRepository.findById(inputBrewery.countryId).orElseThrow { NotFoundException("Country not found.") }
        brewery.name = inputBrewery.name
        brewery.country = country
        breweryRepository.save(brewery)
        return BreweryResponseDTO(id = brewery.id, name = brewery.name, countryName = brewery.country.name)
    }
}