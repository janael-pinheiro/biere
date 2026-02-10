package com.biere.catalog.domain.model

data class PaginatedResult<T>(
    val data: T,
    val metadata: PageMetadata)
