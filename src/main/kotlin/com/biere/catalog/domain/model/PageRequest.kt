package com.biere.catalog.domain.model

data class PageRequest(
    val number: Int,
    val size: Int,
    val sort: String
)
