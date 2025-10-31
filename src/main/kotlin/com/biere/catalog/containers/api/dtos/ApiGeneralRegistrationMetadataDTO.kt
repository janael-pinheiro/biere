package com.biere.catalog.containers.api.dtos

import com.fasterxml.jackson.annotation.JsonProperty

data class ApiGeneralRegistrationMetadataDTO(
    @JsonProperty("_links") val links: ApiGeneralRegistrationOperationsDTO)
