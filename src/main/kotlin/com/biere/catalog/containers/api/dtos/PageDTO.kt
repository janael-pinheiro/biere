package com.biere.catalog.containers.api.dtos

import com.fasterxml.jackson.annotation.JsonProperty

data class PageDTO(
    @JsonProperty("total_elements") val totalElements: Long,
    @JsonProperty("total_pages") val totalPages: Int,
    val first: String?,
    val last: String?,
    val next: String?,
    val previous: String?) {
}