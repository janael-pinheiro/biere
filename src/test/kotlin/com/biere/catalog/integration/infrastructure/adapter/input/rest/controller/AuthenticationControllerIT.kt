package com.biere.catalog.integration.infrastructure.adapter.input.rest.controller

import com.biere.catalog.infrastructure.adapter.input.rest.dtos.TokenRequestDTO
import com.biere.catalog.infrastructure.adapter.input.rest.dtos.TokenResponseDTO
import com.biere.catalog.infrastructure.adapter.output.persistence.entity.UserEntity
import com.biere.catalog.infrastructure.adapter.output.persistence.repository.UserRepository
import com.biere.catalog.application.service.UserService
import com.biere.catalog.domain.model.InputUser
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
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = [com.biere.catalog.CatalogApplication::class])
@ActiveProfiles("test")
class AuthenticationControllerIT(
    @Autowired private val webTestClient: WebTestClient,
    @Autowired private val userRepository: UserRepository,
    @Autowired private val userService: UserService) {

    @Test
    fun `when invalid user or password, should return unauthorized status`(){
        webTestClient
            .post()
            .uri("/v1/users/login")
            .bodyValue(TokenRequestDTO(email = "test", password = "password"))
            .exchange()
            .expectStatus().isUnauthorized
    }

    @Test
    fun `when valid user or password, should return generated token`(){
        val password = "password"
        val email = "test@email.com"
        userService.registerUser(InputUser(name="test", email = email, password = password))
        webTestClient
            .post()
            .uri("/v1/users/login")
            .bodyValue(TokenRequestDTO(email = email, password = password))
            .exchange()
            .expectStatus().isOk
            .expectBody(TokenResponseDTO::class.java)
            .consumeWith { response -> val tokenResponse = response.responseBody
                assertEquals(3, tokenResponse?.accessToken?.split(".")?.size)

            }
    }
}