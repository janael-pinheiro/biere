package com.biere.catalog.core.services

import com.biere.catalog.containers.api.dtos.CountryRegistrationDTO
import com.biere.catalog.containers.api.dtos.CountryResponseDTO
import com.biere.catalog.core.exceptions.ConflictException
import com.biere.catalog.core.exceptions.NotFoundException
import com.biere.catalog.adapters.entities.CountryEntity
import com.biere.catalog.adapters.output.repositories.CountryRepository
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import java.time.ZonedDateTime

@Service
class CountryService(private val countryRepository: CountryRepository) {
    fun registerCountry(countryRegistrationDTO: CountryRegistrationDTO): CountryResponseDTO {
        val now: ZonedDateTime = ZonedDateTime.now();
        val country = CountryEntity(name = countryRegistrationDTO.name, createdAt = now)
        var savedCountry: CountryEntity? = null
        try{
            savedCountry = countryRepository.save(country)
        } catch (e: DataIntegrityViolationException){
            if(e.message?.contains("duplicate key value violates unique constraint") == true)
                throw ConflictException(message = "Country already registered.")
        }
        return CountryResponseDTO(id = savedCountry?.id, name = savedCountry?.name)
    }

    fun getCountries(): List<CountryResponseDTO> {
        return this.countryRepository.findAll().stream().map { country -> CountryResponseDTO(id = country.id, name = country.name) }.toList()
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