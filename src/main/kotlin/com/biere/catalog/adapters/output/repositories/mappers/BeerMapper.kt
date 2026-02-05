package com.biere.catalog.adapters.output.repositories.mappers

import com.biere.catalog.containers.api.dtos.BeerRegistrationDTO
import com.biere.catalog.containers.api.dtos.BeerResponseDTO
import com.biere.catalog.core.models.InputBeerModel
import com.biere.catalog.core.models.OutputBeerModel

interface BeerMapper {
    companion object {

        fun mapToInputBeer(input: BeerRegistrationDTO): InputBeerModel {
            return InputBeerModel(
                name = input.name,
                alcoholContent = input.alcoholContent!!,
                breweryId = input.breweryId!!,
                styleId = input.styleId!!,
                year = input.year!!)
        }

        fun mapToBeerResponseDTO(input: OutputBeerModel): BeerResponseDTO {
            return BeerResponseDTO(
                id = input.id,
                name = input.name,
                countryName = input.countryName,
                alcoholContent = input.alcoholContent,
                brewery = input.brewery,
                style = input.style,
                year = input.year)
        }
    }
}