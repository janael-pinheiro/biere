package com.biere.catalog.core.services

import com.biere.catalog.core.dto.CountryRegistrationDTO
import com.biere.catalog.core.dto.CountryResponseDTO
import com.biere.catalog.core.dto.MultipleCountriesResponseDTO
import com.biere.catalog.core.exceptions.ConflictException
import com.biere.catalog.core.exceptions.NotFoundException
import com.biere.catalog.adapters.entities.CountryEntity
import com.biere.catalog.adapters.repositories.CountryRepository
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import java.time.ZonedDateTime

@Service
class CountryService(private val countryRepository: CountryRepository) {
    fun registerCountry(countryRegistrationDTO: CountryRegistrationDTO): Long? {
        val now: ZonedDateTime = ZonedDateTime.now();
        val country = CountryEntity(name = countryRegistrationDTO.name, createdAt = now)
        var countryId: Long? = null
        try{
            val savedCountry: CountryEntity = countryRepository.save(country)
            countryId = savedCountry.id
        } catch (e: DataIntegrityViolationException){
            if(e.message?.contains("duplicate key value violates unique constraint") == true)
                throw ConflictException(message = "Country already registered.")
        }
        return countryId
    }

    fun getCountries(): MultipleCountriesResponseDTO {
        val countries =
            MultipleCountriesResponseDTO(countries = this.countryRepository.findAll().stream().map { country -> CountryResponseDTO(id = country.id, name = country.name) }.toList())
        return countries
    }

    fun getSpecificCountry(countryId: Long): CountryResponseDTO{
        val country = this.countryRepository.findById(countryId)
        if(country.isEmpty) {
            throw NotFoundException("Country not found.")
        }
        return CountryResponseDTO(id = country.get().id, name = country.get().name)
    }

    fun deleteCountry(countryId: Long) {
        this.countryRepository.deleteById(countryId)
    }
}