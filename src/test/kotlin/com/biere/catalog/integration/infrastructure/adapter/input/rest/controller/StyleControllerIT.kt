package com.biere.catalog.integration.infrastructure.adapter.input.rest.controller

import com.biere.catalog.integration.configuration.PostgresTestContainersConfiguration
import com.biere.catalog.infrastructure.adapter.output.persistence.repository.StyleRepository
import com.biere.catalog.infrastructure.adapter.input.rest.dtos.ApiCollectionResponseDTO
import com.biere.catalog.infrastructure.adapter.input.rest.dtos.ApiIndividualResponseDTO
import com.biere.catalog.infrastructure.adapter.input.rest.dtos.StyleRegistrationDTO
import com.biere.catalog.infrastructure.adapter.input.rest.dtos.StyleResponseDTO
import com.biere.catalog.infrastructure.adapter.input.rest.dtos.StyleUpdateRequestDTO
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
import kotlin.test.assertNotNull

@AutoConfigureWebTestClient
@Import(PostgresTestContainersConfiguration::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = [com.biere.catalog.CatalogApplication::class])
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

    @Test
    fun `fetches styles`(){
        webTestClient
            .get()
            .uri(stylesUri)
            .exchange()
            .expectStatus().isOk
            .expectBody(ApiCollectionResponseDTO::class.java)
            .consumeWith { response -> val styles = response.responseBody
                assertNotNull(styles?.data)
            }
    }

    @Test
    fun `update a style`(){
        val newName = "test1"
        var updateStyleUrl = ""
        var getStyleUrl = ""

        webTestClient
            .post()
            .uri(stylesUri)
            .bodyValue(newStyle)
            .exchange()
            .expectBody(Map::class.java)
            .consumeWith { response ->
                val links = response.responseBody?.get("_links") as Map<*, *>
                updateStyleUrl = (links["update_style"] as Map<*, *>)["href"] as String
                getStyleUrl = (links["self"] as Map<*, *>)["href"] as String
            }

        webTestClient
            .put()
            .uri(updateStyleUrl)
            .bodyValue(StyleUpdateRequestDTO(name = newName))
            .exchange()
            .expectStatus().isOk

        webTestClient
            .get()
            .uri(getStyleUrl)
            .exchange()
            .expectStatus().isOk
            .expectBody(ApiIndividualResponseDTO::class.java)
            .consumeWith { response -> val style = response.responseBody?.data as? Map<*, *>
                assertEquals(newName, style?.get("name"))
            }
    }

    @Test
    fun `delete a style`(){
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
            .delete()
            .uri(styleUrl)
            .exchange()
            .expectStatus().isNoContent
    }
}