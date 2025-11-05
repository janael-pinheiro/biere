package com.biere.catalog.core.models

data class UpdateBeerModel(
    val name: String?,
    val alcoholContent: Float?,
    val breweryId: Long?,
    val styleId: Long?,
    val year: Long?
)
