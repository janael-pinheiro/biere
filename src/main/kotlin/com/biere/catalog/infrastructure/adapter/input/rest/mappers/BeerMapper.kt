package com.biere.catalog.infrastructure.adapter.input.rest.mappers

import com.biere.catalog.infrastructure.adapter.input.rest.dtos.BeerRegistrationDTO
import com.biere.catalog.infrastructure.adapter.input.rest.dtos.BeerResponseDTO
import com.biere.catalog.domain.model.InputBeerModel
import com.biere.catalog.domain.model.OutputBeerModel

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