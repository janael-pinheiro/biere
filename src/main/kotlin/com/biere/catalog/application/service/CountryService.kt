package com.biere.catalog.application.service

import com.biere.catalog.domain.port.output.CountryOutputPort
import com.biere.catalog.domain.port.input.CountryUseCase
import com.biere.catalog.domain.model.CountryModel
import com.biere.catalog.domain.exception.ConflictException
import com.biere.catalog.domain.exception.NotFoundException
import org.springframework.stereotype.Service

@Service
class CountryService(private val countryOutputPort: CountryOutputPort) : CountryUseCase {
    override fun register(name: String): CountryModel {
        if (countryOutputPort.existsByName(name)) {
            throw ConflictException("The name ${name} already exists.")
        }
        return countryOutputPort.save(name)
    }

    override fun getCountries(): List<CountryModel> {
        return countryOutputPort.findAll()
    }

    override fun getSpecificCountry(countryId: Long): CountryModel{
        return countryOutputPort.findById(countryId)
    }

    override fun deleteCountry(countryId: Long) {
        if (!countryOutputPort.existsById(countryId)) {
            throw NotFoundException("Country not found.")
        }
        countryOutputPort.delete(countryId)
    }

    override fun updateCountry(
        countryId: Long,
        name: String): CountryModel {
        if (!countryOutputPort.existsById(countryId)) {
            throw NotFoundException("Country not found.")
        }
        return countryOutputPort.update(countryId, name)
    }
}