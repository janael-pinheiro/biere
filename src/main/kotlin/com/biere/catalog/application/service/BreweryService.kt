package com.biere.catalog.application.service

import com.biere.catalog.domain.port.output.BreweryOutputPort
import com.biere.catalog.domain.port.input.BreweryUseCase
import com.biere.catalog.domain.model.BreweryModel
import com.biere.catalog.domain.exception.ConflictException
import com.biere.catalog.domain.exception.NotFoundException
import org.springframework.stereotype.Service

@Service
class BreweryService(private val breweryOutputPort: BreweryOutputPort) : BreweryUseCase {
    override fun register(name: String, countryId: Long): BreweryModel {
        if (breweryOutputPort.existsByName(name)){
            throw ConflictException("The name ${name} already exists.")
        }
        return breweryOutputPort.save(name, countryId)
    }

    override fun getBreweries(): List<BreweryModel> {
        return breweryOutputPort.findAll()
    }

    override fun getSpecificBrewery(breweryId: Long): BreweryModel {
        return breweryOutputPort.findById(breweryId)
    }

    override fun updateBrewery(breweryId: Long, name: String, countryId: Long): BreweryModel {
        if (!breweryOutputPort.existsById(breweryId)) {
            throw NotFoundException("Brewery not found.")
        }
        return breweryOutputPort.update(breweryId, name, countryId)
    }

    override fun deleteBrewery(breweryId: Long) {
        if (!breweryOutputPort.existsById(breweryId)){
            throw NotFoundException("Brewery not found.")
        }
        breweryOutputPort.delete(breweryId)
    }
}