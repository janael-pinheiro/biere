package com.biere.catalog.core.models

data class PaginatedResult<T>(
    val data: T,
    val metadata: PageMetadata)
