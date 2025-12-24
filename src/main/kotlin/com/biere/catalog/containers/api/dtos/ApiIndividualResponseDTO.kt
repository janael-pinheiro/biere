package com.biere.catalog.containers.api.dtos

import org.springframework.hateoas.RepresentationModel

data class ApiIndividualResponseDTO<T>(val data: T): RepresentationModel<ApiIndividualResponseDTO<T>>()
