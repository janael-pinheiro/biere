package com.biere.catalog.integration.containers.api.controllers

import com.biere.catalog.integration.configuration.PostgresTestContainersConfiguration
import com.biere.catalog.adapters.output.repositories.StyleRepository
import com.biere.catalog.containers.api.dtos.ApiIndividualResponseDTO
import com.biere.catalog.containers.api.dtos.StyleRegistrationDTO
import com.biere.catalog.containers.api.dtos.StyleResponseDTO
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import kotlin.test.assertEquals

@AutoConfigureWebTestClient
@Import(PostgresTestContainersConfiguration::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = [com.biere.catalog.containers.api.CatalogApplication::class])
@ActiveProfiles("test")
class StyleControllerIT(
    @Autowired private val webTestClient: WebTestClient,
    @Autowired private val styleRepository: StyleRepository) {

    private val stylesUri = "/v1/styles"
    private lateinit var newStyle: StyleRegistrationDTO

    @BeforeEach
    fun setup(){
        newStyle = StyleRegistrationDTO(name = "lager")
    }

    @AfterEach
    fun tearDown(){
        styleRepository.deleteAll()
    }

    @Test
    fun `register style`(){
        webTestClient
            .post()
            .uri(stylesUri)
            .bodyValue(newStyle)
            .exchange()
            .expectStatus().isCreated
            .expectBody()
            .jsonPath("$._links.self.href").exists()
            .jsonPath("$._links.update_style.href").exists()
            .jsonPath("$._links.get_all_styles.href").exists()
    }

    @Test
    fun `get a specific style`(){
        var styleUrl = ""

        webTestClient
            .post()
            .uri(stylesUri)
            .bodyValue(newStyle)
            .exchange()
            .expectStatus().isCreated
            .expectBody(Map::class.java)
            .consumeWith { response ->
                val links = response.responseBody?.get("_links") as Map<*, *>
                styleUrl = (links["self"] as Map<*, *>)["href"] as String
            }

        webTestClient
            .get()
            .uri(styleUrl)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.data.name").isEqualTo(newStyle.name)
            .jsonPath("$._links.self.href").exists()
    }
}