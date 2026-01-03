package com.biere.catalog.core.services

import com.biere.catalog.adapters.entities.BreweryEntity
import com.biere.catalog.adapters.output.repositories.BreweryRepository
import com.biere.catalog.adapters.output.repositories.CountryRepository
import com.biere.catalog.containers.api.dtos.BreweryRegistrationDTO
import com.biere.catalog.containers.api.dtos.BreweryResponseDTO
import com.biere.catalog.containers.api.dtos.BreweryUpdateRequestDTO
import com.biere.catalog.core.exceptions.ConflictException
import com.biere.catalog.core.exceptions.NotFoundException
import com.biere.catalog.core.models.BreweryModel
import com.biere.catalog.core.models.CountryModel
import org.springframework.stereotype.Service

@Service
class BreweryService(private val breweryRepository: BreweryRepository, private val countryRepository: CountryRepository) {
    fun register(inputBrewery: BreweryRegistrationDTO): BreweryModel {
        if (breweryRepository.existsByName(inputBrewery.name)){
            throw ConflictException("The name ${inputBrewery.name} already exists.")
        }
        val country = countryRepository.findById(inputBrewery.countryId).get()
        val newBrewery = BreweryEntity(name = inputBrewery.name, country = country)
        val savedBrewery = breweryRepository.save(newBrewery)
        val countryModel = CountryModel(country.id!!, country.name)
        return BreweryModel(id = savedBrewery.id ?: 0, name = savedBrewery.name, country = countryModel)
    }

    fun getBreweries(): List<BreweryModel> {
        return this.breweryRepository.findAll().stream().map { brewery -> BreweryModel(
            id = brewery.id ?: 0,
            name = brewery.name,
            country = CountryModel(id = brewery.country.id!!, name = brewery.country.name)) }.toList()
    }

    fun getSpecificBrewery(breweryId: Long): BreweryModel {
        val brewery = breweryRepository.findById(breweryId)
        if(brewery.isEmpty){
            throw NotFoundException("Brewery not found.")
        }
        val country = CountryModel(id = brewery.get().country.id!!, name = brewery.get().country.name)
        return BreweryModel(id = brewery.get().id!!, name = brewery.get().name, country = country)
    }

    fun updateBrewery(breweryId: Long, inputBrewery: BreweryUpdateRequestDTO): BreweryModel {
        val brewery = breweryRepository.findById(breweryId).orElseThrow { NotFoundException("Brewery not found.") }
        val country = countryRepository.findById(inputBrewery.countryId).orElseThrow { NotFoundException("Country not found.") }
        brewery.name = inputBrewery.name
        brewery.country = country
        breweryRepository.save(brewery)
        return BreweryModel(id = brewery.id!!, name = brewery.name, country = CountryModel(id = brewery.country.id!!, name = brewery.country.name))
    }

    fun deleteBrewery(breweryId: Long) {
        if (!breweryRepository.existsById(breweryId)){
            throw NotFoundException("Brewery not found.")
        }
        breweryRepository.deleteById(breweryId)
    }
}