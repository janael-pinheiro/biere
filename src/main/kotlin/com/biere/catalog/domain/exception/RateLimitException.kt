package com.biere.catalog.domain.exception

class RateLimitException(message: String, val retryAfterSeconds: Long) : RuntimeException(message)
