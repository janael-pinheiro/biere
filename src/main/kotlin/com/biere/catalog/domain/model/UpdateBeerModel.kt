package com.biere.catalog.domain.model

data class UpdateBeerModel(
    val name: String?,
    val alcoholContent: Float?,
    val breweryId: Long?,
    val styleId: Long?,
    val year: Long?
)
