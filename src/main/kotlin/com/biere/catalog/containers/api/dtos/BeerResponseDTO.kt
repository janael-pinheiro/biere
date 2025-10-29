package com.biere.catalog.containers.api.dtos

data class BeerResponseDTO(
    val id: Long,
    val name: String,
    val countryName: String,
    val alcoholContent: Float,
    val brewery: String,
    val style: String)