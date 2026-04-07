package com.biere.catalog.infrastructure.configuration

import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class InMemoryIdempotencyRepository : IdempotencyRepository {
    private val storage = ConcurrentHashMap<String, IdempotencyResponse>()

    override fun get(key: String): IdempotencyResponse? = storage[key]

    override fun save(key: String, response: IdempotencyResponse) {
        // In a real scenario, we would have an expiration time
        storage[key] = response
    }
}
