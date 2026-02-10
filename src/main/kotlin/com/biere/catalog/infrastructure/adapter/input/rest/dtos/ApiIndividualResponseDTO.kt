package com.biere.catalog.infrastructure.adapter.input.rest.dtos

import org.springframework.hateoas.RepresentationModel
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Generic wrapper for a single resource response")
data class ApiIndividualResponseDTO<T>(
    @Schema(description = "The data object")
    val data: T
): RepresentationModel<ApiIndividualResponseDTO<T>>()
