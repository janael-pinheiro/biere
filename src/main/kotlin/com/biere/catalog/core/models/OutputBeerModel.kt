package com.biere.catalog.core.models

data class OutputBeerModel(
    val id: Long,
    val name: String,
    val countryName: String,
    val alcoholContent: Float,
    val brewery: String,
    val style: String,
    val year: Long)
