package com.biere.catalog.core.services

import com.biere.catalog.adapters.entities.CountryEntity
import com.biere.catalog.adapters.output.repositories.CountryRepository
import com.biere.catalog.containers.api.dtos.CountryRegistrationDTO
import com.biere.catalog.containers.api.dtos.CountryResponseDTO
import com.biere.catalog.containers.api.dtos.CountryUpdateRequestDTO
import com.biere.catalog.core.exceptions.ConflictException
import com.biere.catalog.core.exceptions.NotFoundException
import com.biere.catalog.core.models.CountryModel
import org.springframework.stereotype.Service
import java.time.ZonedDateTime

@Service
class CountryService(private val countryRepository: CountryRepository) {
    fun registerCountry(countryRegistrationDTO: CountryRegistrationDTO): CountryModel {
        if (countryRepository.existsByName(countryRegistrationDTO.name)) {
            throw ConflictException("The name ${countryRegistrationDTO.name} already exists.")
        }
        val now: ZonedDateTime = ZonedDateTime.now();
        val country = CountryEntity(name = countryRegistrationDTO.name, createdAt = now)
        var savedCountry: CountryEntity? = null
        savedCountry = countryRepository.save(country)
        return CountryModel(id = savedCountry.id!!, name = savedCountry.name)
    }

    fun getCountries(): List<CountryModel> {
        return this.countryRepository.findAll().stream().map { country -> CountryModel(id = country.id!!, name = country.name) }.toList()
    }

    fun getSpecificCountry(countryId: Long): CountryModel{
        val country = this.countryRepository.findById(countryId)
        if(country.isEmpty) {
            throw NotFoundException("Country not found.")
        }
        return CountryModel(id = country.get().id!!, name = country.get().name)
    }

    fun deleteCountry(countryId: Long) {
        this.countryRepository.deleteById(countryId)
    }

    fun updateCountry(
        countryId: Long,
        countryUpdateDTO: CountryUpdateRequestDTO): CountryModel {
        val country = this.countryRepository.findById(countryId).orElseThrow { NotFoundException("Country not found") }
        country.name = countryUpdateDTO.name
        this.countryRepository.save(country)
        return CountryModel(id = country.id!!, name = country.name)
    }
}