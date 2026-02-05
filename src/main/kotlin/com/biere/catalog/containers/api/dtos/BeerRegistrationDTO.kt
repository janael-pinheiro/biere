package com.biere.catalog.containers.api.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class BeerRegistrationDTO(
    @field:NotEmpty(message = "The name of the beer cannot be empty")
    @field:Size(message = "The name of the beer is too long", max = 100)
    val name: String,

    @JsonProperty("alcohol_content")
    @field:NotNull(message = "Alcohol content can't be null")
    @field:Max(value = 100, message = "Alcohol content can't be greater than 100%")
    val alcoholContent: Float?,

    @JsonProperty("brewery_id")
    @field:NotNull(message = "breweryId can't be null")
    val breweryId: Long?,

    @JsonProperty("style_id")
    @field:NotNull(message = "styleId can't be null")
    val styleId: Long?,

    @field:NotNull(message = "year can't be null")
    val year: Long?
)