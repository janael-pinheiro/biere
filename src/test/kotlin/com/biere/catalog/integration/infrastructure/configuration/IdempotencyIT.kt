package com.biere.catalog.integration.infrastructure.configuration

import com.biere.catalog.integration.configuration.PostgresTestContainersConfiguration
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.UUID

@AutoConfigureWebTestClient
@Import(PostgresTestContainersConfiguration::class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [com.biere.catalog.CatalogApplication::class],
    properties = ["idempotency.enabled=true"]
)
@ActiveProfiles("test")
class IdempotencyIT(
    @Autowired private val webTestClient: WebTestClient
) {

    @Test
    fun `should return cached response for same idempotency key`() {
        // First, ensure we are authenticated if needed, but let's try a permitAll path first or mock it.
        // For this test, let's use a path that is simple. 
        // Actually, let's use a valid login to get a token and then test a protected path.
        
        // Assuming there is a migrations/seed for this user or we create it.
        // If not, let's use a path that gives 404 but is still cached.
        
        val key = UUID.randomUUID().toString()
        val uri = "/v1/non-existent-resource" 

        // First request
        val firstResponse = webTestClient
            .post()
            .uri(uri)
            .header("X-Idempotency-Key", key)
            .exchange()
            .expectStatus().isNotFound
            .expectHeader().doesNotExist("X-Idempotency-Cache")
            .returnResult(String::class.java)

        val firstBody = firstResponse.responseBodyContent

        // Second request with same key
        webTestClient
            .post()
            .uri(uri)
            .header("X-Idempotency-Key", key)
            .exchange()
            .expectStatus().isNotFound
            .expectHeader().valueEquals("X-Idempotency-Cache", "HIT")
            .expectBody(ByteArray::class.java).isEqualTo(firstBody!!)
    }

    @Test
    fun `should return 400 Bad Request when idempotency key is missing for POST`() {
        val uri = "/v1/styles" // Any state-changing endpoint

        webTestClient
            .post()
            .uri(uri)
            // No X-Idempotency-Key header
            .exchange()
            .expectStatus().isBadRequest
            .expectBody()
            .jsonPath("$.type").isEqualTo("https://biere.catalog.com/problem/idempotency-key-missing")
            .jsonPath("$.remediation").exists()
    }
}
