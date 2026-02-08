package com.biere.catalog.containers.api.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "DTO for brewery registration request")
data class BreweryRegistrationDTO(
    @field:NotEmpty(message = "The name of the brewery can not be empty")
    @field:Size(max=100)
    @Schema(description = "Name of the brewery", example = "Heineken Brewery", maxLength = 100, minLength = 1, requiredMode = Schema.RequiredMode.REQUIRED)
    val name: String,

    @JsonProperty("country_id")
    @field:NotNull(message = "CountryId can't be null")
    @Schema(description = "ID of the country", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    val countryId: Long?
)