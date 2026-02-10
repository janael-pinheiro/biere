package com.biere.catalog.domain.model

data class TokenModel(
    val accessToken: String,
    val refreshToken: String)
