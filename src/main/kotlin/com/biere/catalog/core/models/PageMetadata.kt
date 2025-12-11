package com.biere.catalog.core.models

data class PageMetadata(
    val totalElements: Long,
    val totalPages: Int,
    val first: String?,
    val last: String?,
    val next: String?,
    val previous: String?,
    val current: Int?)
