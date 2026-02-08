package com.biere.catalog.containers.api.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import com.opencsv.bean.CsvBindByName
import io.swagger.v3.oas.annotations.media.Schema


@Schema(description = "DTO for beer response data")
data class BeerResponseDTO(
    @CsvBindByName(column = "id")
    @Schema(description = "Unique identifier of the beer", example = "1")
    val id: Long,
    @CsvBindByName(column = "name")
    @Schema(description = "Name of the beer", example = "Heineken")
    val name: String,
    @JsonProperty("country_name")
    @CsvBindByName(column = "country_name")
    @Schema(description = "Country of origin", example = "Netherlands")
    val countryName: String,
    @JsonProperty("alcohol_content")
    @CsvBindByName(column = "alcohol_content")
    @Schema(description = "Alcohol content percentage", example = "5.0")
    val alcoholContent: Float,
    @CsvBindByName(column = "brewery")
    @Schema(description = "Name of the brewery", example = "Heineken Brewery")
    val brewery: String,
    @CsvBindByName(column = "style")
    @Schema(description = "Style of the beer", example = "Lager")
    val style: String,
    @CsvBindByName(column = "year")
    @Schema(description = "Year of production", example = "2023")
    val year: Long)