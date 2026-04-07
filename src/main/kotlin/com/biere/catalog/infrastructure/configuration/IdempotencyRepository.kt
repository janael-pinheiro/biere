package com.biere.catalog.infrastructure.configuration

import java.io.Serializable

data class IdempotencyResponse(
    val status: Int,
    val contentType: String?,
    val body: ByteArray,
    val headers: Map<String, List<String>>
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as IdempotencyResponse
        if (status != other.status) return false
        if (contentType != other.contentType) return false
        if (!body.contentEquals(other.body)) return false
        return headers == other.headers
    }

    override fun hashCode(): Int {
        var result = status
        result = 31 * result + (contentType?.hashCode() ?: 0)
        result = 31 * result + body.contentHashCode()
        result = 31 * result + headers.hashCode()
        return result
    }
}

interface IdempotencyRepository {
    fun get(key: String): IdempotencyResponse?
    fun save(key: String, response: IdempotencyResponse)
}
