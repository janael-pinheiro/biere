package com.biere.catalog.domain.model

data class BreweryModel(
    val id: Long,
    val name: String,
    val country: CountryModel)
