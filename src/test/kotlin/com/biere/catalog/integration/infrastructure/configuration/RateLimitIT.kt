package com.biere.catalog.integration.infrastructure.configuration

import com.biere.catalog.integration.configuration.PostgresTestContainersConfiguration
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient

@AutoConfigureWebTestClient
@Import(PostgresTestContainersConfiguration::class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [com.biere.catalog.CatalogApplication::class],
    properties = ["rate-limit.capacity=5", "rate-limit.refill-tokens=5"]
)
@ActiveProfiles("test")
class RateLimitIT(
    @Autowired private val webTestClient: WebTestClient
) {

    @Test
    fun `should enforce rate limit after 5 requests`() {
        val uri = "/v1/countries"

        // Execute 5 successful requests
        for (i in 1..5) {
            webTestClient
                .get()
                .uri(uri)
                .exchange()
                .expectStatus().isOk
                .expectHeader().exists("X-Rate-Limit-Remaining")
        }

        // The 6th request should be rate limited
        webTestClient
            .get()
            .uri(uri)
            .exchange()
            .expectStatus().isEqualTo(429)
            .expectHeader().exists("X-Rate-Limit-Retry-After-Seconds")
            .expectBody()
            .jsonPath("$.message").isEqualTo("You have exhausted your API Request Quota")
    }

    @Test
    fun `should have different buckets for different IP addresses`() {
        val uri = "/v1/countries"

        // Exhaust bucket for IP 1.1.1.1
        for (i in 1..5) {
            webTestClient
                .get()
                .uri(uri)
                .header("X-Forwarded-For", "1.1.1.1")
                .exchange()
                .expectStatus().isOk
        }

        // 6th request for IP 1.1.1.1 is limited
        webTestClient
            .get()
            .uri(uri)
            .header("X-Forwarded-For", "1.1.1.1")
            .exchange()
            .expectStatus().isEqualTo(429)

        // IP 2.2.2.2 should still have tokens
        webTestClient
            .get()
            .uri(uri)
            .header("X-Forwarded-For", "2.2.2.2")
            .exchange()
            .expectStatus().isOk
            .expectHeader().valueEquals("X-Rate-Limit-Remaining", "4")
    }
}
