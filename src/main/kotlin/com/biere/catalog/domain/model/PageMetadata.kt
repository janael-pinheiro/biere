package com.biere.catalog.domain.model

data class PageMetadata(
    val totalElements: Long,
    val totalPages: Int,
    val first: Int,
    val last: Int,
    val next: Int,
    val previous: Int,
    val current: Int?)
