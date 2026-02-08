package com.biere.catalog.containers.api.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Pagination information metadata")
data class PageDTO(
    @JsonProperty("total_elements")
    @Schema(description = "Total number of elements across all pages", example = "100")
    val totalElements: Long,
    
    @JsonProperty("total_pages")
    @Schema(description = "Total number of pages", example = "10")
    val totalPages: Int,
    
    @Schema(description = "Link to the first page", example = "http://api.example.com/v1/beers?page=0")
    val first: String?,
    
    @Schema(description = "Link to the last page", example = "http://api.example.com/v1/beers?page=9")
    val last: String?,
    
    @Schema(description = "Link to the next page", example = "http://api.example.com/v1/beers?page=2")
    val next: String?,
    
    @Schema(description = "Link to the previous page", example = "http://api.example.com/v1/beers?page=0")
    val previous: String?,
    
    @Schema(description = "Current page number (zero-indexed)", example = "1")
    val current: Int?
)