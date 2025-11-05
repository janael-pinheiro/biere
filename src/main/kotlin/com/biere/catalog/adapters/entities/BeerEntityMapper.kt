package com.biere.catalog.adapters.entities

import com.biere.catalog.core.models.InputBeerModel
import com.biere.catalog.core.models.OutputBeerModel

interface BeerEntityMapper {
    companion object {
        fun mapToEntity(input: InputBeerModel, brewery: BreweryEntity, style: StyleEntity): BeerEntity {
            return BeerEntity(
                name = input.name,
                alcoholContent = input.alcoholContent,
                brewery = brewery,
                style = style,
                year = input.year)
        }

        fun mapToOutputBeer(input: BeerEntity): OutputBeerModel {
            return OutputBeerModel(
                id = input.id ?: 0,
                name = input.name,
                countryName = input.brewery.country.name,
                alcoholContent = input.alcoholContent,
                brewery = input.brewery.name,
                style = input.style.name,
                year = input.year)
        }
    }
}