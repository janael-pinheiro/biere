package com.biere.catalog.infrastructure.adapter.output.persistence.repository

import com.biere.catalog.domain.model.BreweryModel
import com.biere.catalog.domain.model.CountryModel
import com.biere.catalog.domain.port.output.BreweryOutputPort
import com.biere.catalog.infrastructure.adapter.output.persistence.entity.BreweryEntity
import com.biere.catalog.domain.exception.NotFoundException
import org.springframework.stereotype.Component

@Component
class BreweryOutputAdapter(
    private val breweryRepository: BreweryRepository,
    private val countryRepository: CountryRepository
) : BreweryOutputPort {

    override fun save(name: String, countryId: Long): BreweryModel {
        val country = countryRepository.findById(countryId).orElseThrow { NotFoundException(message = "Country $countryId not found.", remediation = "You need to check if a country with that id has already been registered or correct the country's id.") }
        val brewery = breweryRepository.save(BreweryEntity(name = name, country = country))
        return mapToModel(brewery)
    }

    override fun findAll(): List<BreweryModel> {
        return breweryRepository.findAll().map { mapToModel(it) }
    }

    override fun findById(id: Long): BreweryModel {
        val brewery = breweryRepository.findById(id).orElseThrow { NotFoundException(message = "Brewery $id not found.", remediation = "You need to check if a brewery with that id has already been registered or correct the brewery's id.") }
        return mapToModel(brewery)
    }

    override fun update(id: Long, name: String, countryId: Long): BreweryModel {
        val brewery = breweryRepository.findById(id).orElseThrow { NotFoundException(message = "Brewery $id not found.", remediation = "You need to check if a brewery with that id has already been registered or correct the brewery's id.") }
        val country = countryRepository.findById(countryId).orElseThrow { NotFoundException(message = "Country $countryId not found.", remediation = "You need to check if a country with that id has already been registered or correct the country's id.") }
        brewery.name = name
        brewery.country = country
        return mapToModel(breweryRepository.save(brewery))
    }

    override fun delete(id: Long) {
        breweryRepository.deleteById(id)
    }

    override fun existsByName(name: String): Boolean {
        return breweryRepository.existsByName(name)
    }

    override fun existsById(id: Long): Boolean {
        return breweryRepository.existsById(id)
    }

    private fun mapToModel(entity: BreweryEntity): BreweryModel {
        return BreweryModel(
            id = entity.id ?: 0,
            name = entity.name,
            country = CountryModel(id = entity.country.id!!, name = entity.country.name)
        )
    }
}
