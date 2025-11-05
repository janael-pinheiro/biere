package com.biere.catalog.core.models

data class PageRequest(
    val number: Int,
    val size: Int,
    val sort: String
)
