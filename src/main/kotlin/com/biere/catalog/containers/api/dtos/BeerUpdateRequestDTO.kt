package com.biere.catalog.containers.api.dtos

import com.fasterxml.jackson.annotation.JsonProperty


data class BeerUpdateRequestDTO(
    val name: String?,
    @JsonProperty("alcohol_content") val alcoholContent: Float?,
    @JsonProperty("brewery_id") val breweryId: Long?,
    @JsonProperty("style_id") val styleId: Long?,
    val year: Long?)