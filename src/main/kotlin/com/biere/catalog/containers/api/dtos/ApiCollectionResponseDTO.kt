package com.biere.catalog.containers.api.dtos

import org.springframework.hateoas.RepresentationModel

class ApiCollectionResponseDTO<T>(val data: T, val page: PageDTO?): RepresentationModel<ApiCollectionResponseDTO<T>>() {
}