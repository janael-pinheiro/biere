package com.biere.catalog.infrastructure.adapter.input.rest.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "DTO for beer registration request")
data class BeerRegistrationDTO(
    @field:NotEmpty(message = "The name of the beer can not be empty")
    @field:Size(message = "The name of the beer is too long", max = 100)
    @Schema(description = "Name of the beer", example = "Heineken", maxLength = 100, minLength = 1, requiredMode = Schema.RequiredMode.REQUIRED)
    val name: String,

    @JsonProperty("alcohol_content")
    @field:NotNull(message = "Alcohol content can't be null")
    @field:Max(value = 100, message = "Alcohol content can't be greater than 100%")
    @Schema(description = "Alcohol content percentage", example = "5.0", minimum = "0.0", maximum = "100.0", requiredMode = Schema.RequiredMode.REQUIRED)
    val alcoholContent: Float?,

    @JsonProperty("brewery_id")
    @field:NotNull(message = "breweryId can't be null")
    @Schema(description = "ID of the brewery", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    val breweryId: Long?,

    @JsonProperty("style_id")
    @field:NotNull(message = "styleId can't be null")
    @Schema(description = "ID of the beer style", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    val styleId: Long?,

    @field:NotNull(message = "year can't be null")
    @Schema(description = "Year of production", example = "2023", requiredMode = Schema.RequiredMode.REQUIRED)
    val year: Long?
)