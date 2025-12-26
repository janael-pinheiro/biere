package com.biere.catalog.integration.containers.api.controllers

import com.biere.catalog.adapters.output.repositories.CountryRepository
import com.biere.catalog.containers.api.dtos.ApiCollectionResponseDTO
import com.biere.catalog.containers.api.dtos.ApiIndividualResponseDTO
import com.biere.catalog.containers.api.dtos.CountryRegistrationDTO
import com.biere.catalog.integration.configuration.PostgresTestContainersConfiguration
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@AutoConfigureWebTestClient
@Import(PostgresTestContainersConfiguration::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = [com.biere.catalog.containers.api.CatalogApplication::class])
@ActiveProfiles("test")
class CountryControllerIT(@Autowired var webTestClient: WebTestClient, @Autowired val countryRepository: CountryRepository) {
    private val newCountryName = "Netherlands"
    private val countriesUri = "/v1/countries"

    @AfterEach
    fun tearDown(){
        countryRepository.deleteAll()
    }

    @Test
    fun `fetches countries`(){
        webTestClient
            .get()
            .uri(countriesUri)
            .exchange()
            .expectStatus().isOk
            .expectBody(ApiCollectionResponseDTO::class.java)
            .consumeWith { response -> val countries = response.responseBody
                assertNotNull(countries?.data)
            }
    }

    @Test
    fun `register country`(){
        webTestClient
            .post()
            .uri(countriesUri)
            .bodyValue(CountryRegistrationDTO(name = newCountryName))
            .exchange()
            .expectStatus().isCreated
            .expectBody(ApiIndividualResponseDTO::class.java)
            .consumeWith { response -> val country = response.responseBody
                assertEquals(2, country?.links?.toList()?.size)
            }

        webTestClient
            .get()
            .uri(countriesUri)
            .exchange()
            .expectStatus().isOk
            .expectBody(ApiIndividualResponseDTO::class.java)
    }

    @Test
    fun `get a specific country`(){
        var countryUrl = ""

        webTestClient
            .post()
            .uri(countriesUri)
            .bodyValue(CountryRegistrationDTO(newCountryName))
            .exchange()
            .expectStatus().isCreated
            .expectBody(ApiIndividualResponseDTO::class.java)
            .consumeWith { response -> val countries = response.responseBody
                countryUrl =
                    countries?.links?.filter{ link -> link.toString().contains("GET")}?.get(0).toString().split(" ")[1]
            }

        webTestClient
            .get()
            .uri(countryUrl)
            .exchange()
            .expectStatus().isOk
            .expectBody(CountryRegistrationDTO::class.java)
            .consumeWith { response -> val country = response.responseBody
                assertEquals(newCountryName, country?.name)
            }
    }

    @Test
    fun `delete a country`(){
        var countryUrl = ""

        webTestClient
            .post()
            .uri(countriesUri)
            .bodyValue(CountryRegistrationDTO(newCountryName))
            .exchange()
            .expectStatus().isCreated
            .expectBody(ApiIndividualResponseDTO::class.java)
            .consumeWith { response -> val countries = response.responseBody
                countryUrl =
                    countries?.links?.filter{ link -> link.toString().contains("DELETE")}?.get(0).toString().split(" ")[1]
            }

        webTestClient
            .delete()
            .uri(countryUrl)
            .exchange()
            .expectStatus().isNoContent
    }
}