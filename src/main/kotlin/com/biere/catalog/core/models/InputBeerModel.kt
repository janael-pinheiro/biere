package com.biere.catalog.core.models

data class InputBeerModel(
    val name: String,
    val countryId: Long,
    val alcoholContent: Float,
    val breweryId: Long,
    val styleId: Long,
    val year: Long)
