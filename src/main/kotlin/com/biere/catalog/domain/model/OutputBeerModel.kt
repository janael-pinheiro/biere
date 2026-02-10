package com.biere.catalog.domain.model

data class OutputBeerModel(
    val id: Long,
    val name: String,
    val countryName: String,
    val countryId: Long,
    val alcoholContent: Float,
    val brewery: String,
    val breweryId: Long,
    val style: String,
    val styleId: Long,
    val year: Long)
