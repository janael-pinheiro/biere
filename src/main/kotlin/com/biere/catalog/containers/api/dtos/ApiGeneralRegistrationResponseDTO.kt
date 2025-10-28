package com.biere.catalog.containers.api.dtos

import com.fasterxml.jackson.annotation.JsonProperty

class ApiGeneralRegistrationResponseDTO<T>(val data: T, @JsonProperty("_links") val links: List<String>) {
}