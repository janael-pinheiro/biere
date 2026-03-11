package com.biere.catalog.application.service

import com.biere.catalog.domain.port.output.CountryOutputPort
import com.biere.catalog.domain.port.input.CountryUseCase
import com.biere.catalog.domain.model.CountryModel
import com.biere.catalog.domain.exception.ConflictException
import com.biere.catalog.domain.exception.NotFoundException
import com.biere.catalog.domain.exception.RemediationMessage
import org.springframework.stereotype.Service

@Service
class CountryService(private val countryOutputPort: CountryOutputPort) : CountryUseCase {
    override fun register(name: String): CountryModel {
        if (countryOutputPort.existsByName(name)) {
            throw ConflictException(message = "The name ${name} already exists.",
                remediation = RemediationMessage.COUNTRY_NAME_CONFLICT_REMEDIATION.message
            )
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
            throw NotFoundException(message = "Country not found.", remediation = RemediationMessage.COUNTRY_NOT_FOUND_REMEDIATION.message)
        }
        countryOutputPort.delete(countryId)
    }

    override fun updateCountry(
        countryId: Long,
        name: String): CountryModel {
        if (!countryOutputPort.existsById(countryId)) {
            throw NotFoundException(message = "Country not found.", remediation = RemediationMessage.COUNTRY_NOT_FOUND_REMEDIATION.message)
        }
        return countryOutputPort.update(countryId, name)
    }
}