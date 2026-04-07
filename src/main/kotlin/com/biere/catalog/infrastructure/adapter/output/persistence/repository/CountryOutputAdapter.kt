package com.biere.catalog.infrastructure.adapter.output.persistence.repository

import com.biere.catalog.domain.model.CountryModel
import com.biere.catalog.domain.port.output.CountryOutputPort
import com.biere.catalog.infrastructure.adapter.output.persistence.entity.CountryEntity
import com.biere.catalog.domain.exception.NotFoundException
import org.springframework.stereotype.Component
import java.time.ZonedDateTime

@Component
class CountryOutputAdapter(
    private val countryRepository: CountryRepository
) : CountryOutputPort {

    override fun save(name: String): CountryModel {
        val country = countryRepository.save(CountryEntity(name = name, createdAt = ZonedDateTime.now()))
        return mapToModel(country)
    }

    override fun findAll(): List<CountryModel> {
        return countryRepository.findAll().map { mapToModel(it) }
    }

    override fun findById(id: Long): CountryModel {
        val country = countryRepository.findById(id).orElseThrow { NotFoundException(message = "Country $id not found.", remediation = "You need to check if a country with that id has already been registered or correct the country's id.") }
        return mapToModel(country)
    }

    override fun update(id: Long, name: String): CountryModel {
        val country = countryRepository.findById(id).orElseThrow { NotFoundException(message = "Country $id not found.", remediation = "You need to check if a country with that id has already been registered or correct the country's id.") }
        country.name = name
        return mapToModel(countryRepository.save(country))
    }

    override fun delete(id: Long) {
        countryRepository.deleteById(id)
    }

    override fun existsByName(name: String): Boolean {
        return countryRepository.existsByName(name)
    }

    override fun existsById(id: Long): Boolean {
        return countryRepository.existsById(id)
    }

    private fun mapToModel(entity: CountryEntity): CountryModel {
        return CountryModel(
            id = entity.id ?: 0,
            name = entity.name
        )
    }
}
