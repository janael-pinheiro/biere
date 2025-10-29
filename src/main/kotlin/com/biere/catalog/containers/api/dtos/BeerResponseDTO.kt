package com.biere.catalog.containers.api.dtos

import com.opencsv.bean.CsvBindByName

data class BeerResponseDTO(
    @CsvBindByName(column = "id")
    val id: Long,
    @CsvBindByName(column = "name")
    val name: String,
    @CsvBindByName(column = "country_name")
    val countryName: String,
    @CsvBindByName(column = "alcohol_content")
    val alcoholContent: Float,
    @CsvBindByName(column = "brewery")
    val brewery: String,
    @CsvBindByName(column = "style")
    val style: String,
    @CsvBindByName(column = "year")
    val year: Long)