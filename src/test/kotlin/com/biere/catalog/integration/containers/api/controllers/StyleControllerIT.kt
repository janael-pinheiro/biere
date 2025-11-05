package com.biere.catalog.integration.containers.api.controllers

import com.biere.catalog.integration.configuration.PostgresTestContainersConfiguration
import com.biere.catalog.adapters.output.repositories.StyleRepository
import com.biere.catalog.containers.api.dtos.ApiGeneralRegistrationResponseDTO
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
            .expectBody(ApiGeneralRegistrationResponseDTO::class.java)
            .consumeWith { response -> val style = response.responseBody
                assertEquals(1, style?.links?.size)
            }
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
            .expectBody(ApiGeneralRegistrationResponseDTO::class.java)
            .consumeWith { response -> val styleResponse = response.responseBody
                styleUrl =
                    styleResponse?.links?.stream()?.filter { style -> style.contains("GET") }?.toList()?.get(0).toString().split(" ")[1]
            }

        webTestClient
            .get()
            .uri(styleUrl)
            .exchange()
            .expectStatus().isOk
            .expectBody(StyleResponseDTO::class.java)
            .consumeWith { response -> val style = response.responseBody
                assertEquals(newStyle.name, style?.name)
            }
    }
}