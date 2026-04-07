package com.biere.catalog.domain.exception

class IdempotencyKeyMissingException(
    message: String, 
    val remediation: String
) : Exception(message)
