package com.biere.catalog.containers.api.dtos

import org.springframework.hateoas.RepresentationModel

class ApiGeneralRegistrationResponseDTO<T>(val data: T): RepresentationModel<ApiGeneralRegistrationResponseDTO<T>>() {
}