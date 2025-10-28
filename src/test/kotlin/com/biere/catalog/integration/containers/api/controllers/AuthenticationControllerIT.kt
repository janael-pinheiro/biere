package com.biere.catalog.integration.containers.api.controllers

import com.biere.catalog.containers.api.controllers.authentication.TokenRequestDTO
import com.biere.catalog.containers.api.controllers.authentication.TokenResponseDTO
import com.biere.catalog.adapters.entities.UserEntity
import com.biere.catalog.adapters.repositories.UserRepository
import com.biere.catalog.integration.configuration.PostgresTestContainersConfiguration
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
class AuthenticationControllerIT(
    @Autowired private val webTestClient: WebTestClient,
    @Autowired private val userRepository: UserRepository) {

    @Test
    fun `when invalid user or password, should return unauthorized status`(){
        webTestClient
            .post()
            .uri("/v1/login")
            .bodyValue(TokenRequestDTO(email = "test", password = "password"))
            .exchange()
            .expectStatus().isUnauthorized
    }

    @Test
    fun `when valid user or password, should return generated token`(){
        val password = "password"
        val email = "test@email.com"
        userRepository.save(UserEntity(name="test", email = "test@email.com", password = "password"))
        webTestClient
            .post()
            .uri("/v1/login")
            .bodyValue(TokenRequestDTO(email = email, password = password))
            .exchange()
            .expectStatus().isOk
            .expectBody(TokenResponseDTO::class.java)
            .consumeWith { response -> val tokenResponse = response.responseBody
                assertEquals(3, tokenResponse?.token?.split(".")?.size)

            }
    }
}