package com.biere.catalog.core.models

data class BreweryModel(
    val id: Long,
    val name: String,
    val country: CountryModel)
