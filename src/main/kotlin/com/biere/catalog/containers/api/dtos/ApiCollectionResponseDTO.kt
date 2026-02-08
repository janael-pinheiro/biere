package com.biere.catalog.containers.api.dtos

import org.springframework.hateoas.RepresentationModel
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Generic wrapper for a collection resource response")
class ApiCollectionResponseDTO<T>(
    @Schema(description = "The list of data objects")
    val data: T,
    @Schema(description = "Pagination information")
    val page: PageDTO?
): RepresentationModel<ApiCollectionResponseDTO<T>>()