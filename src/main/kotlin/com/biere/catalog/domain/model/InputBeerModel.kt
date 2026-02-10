package com.biere.catalog.domain.model

data class InputBeerModel(
    val name: String,
    val alcoholContent: Float,
    val breweryId: Long,
    val styleId: Long,
    val year: Long)
