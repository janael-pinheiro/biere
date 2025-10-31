package com.biere.catalog.containers.api.dtos

import com.fasterxml.jackson.annotation.JsonProperty

data class ApiGeneralRegistrationPageDTO(
    val first: Boolean,
    val last: Boolean,
    @JsonProperty("total_elements") val totalElements: Long,
    @JsonProperty("total_pages") val totalPages: Long,
    val current: Long,
    @JsonProperty("has_next") val hasNext: Boolean,
    @JsonProperty("has_previous") val hasPrevious: Boolean)
