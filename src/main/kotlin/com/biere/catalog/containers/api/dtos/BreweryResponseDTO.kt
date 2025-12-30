package com.biere.catalog.containers.api.dtos

data class BreweryResponseDTO(
    val id: Long?,
    val name: String,
    val country: CountryResponseDTO)