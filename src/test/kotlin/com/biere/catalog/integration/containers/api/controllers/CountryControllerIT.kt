package com.biere.catalog.integration.containers.api.controllers

import com.biere.catalog.integration.configuration.PostgresTestContainersConfiguration
import com.biere.catalog.core.dto.CountryRegistrationDTO
import com.biere.catalog.core.dto.CountryRegistrationResponseDTO
import com.biere.catalog.core.dto.MultipleCountriesResponseDTO
import com.biere.catalog.adapters.repositories.CountryRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import kotlin.test.assertEquals
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
            .expectBody(MultipleCountriesResponseDTO::class.java)
            .consumeWith { response -> val countries = response.responseBody
                assertTrue(countries?.countries?.isEmpty() ?: true)
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
            .expectBody(CountryRegistrationResponseDTO::class.java)
            .consumeWith { response -> val country = response.responseBody
                assertEquals(2, country?.links?.size)
            }

        webTestClient
            .get()
            .uri(countriesUri)
            .exchange()
            .expectStatus().isOk
            .expectBody(MultipleCountriesResponseDTO::class.java)
            .consumeWith { response -> val countries = response.responseBody
                assertEquals(1,countries?.countries?.size)
                assertEquals(newCountryName, countries?.countries?.get(0)?.name)
            }
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
            .expectBody(CountryRegistrationResponseDTO::class.java)
            .consumeWith { response -> val countries = response.responseBody
                countryUrl =
                    countries?.links?.stream()?.filter{ country -> country.contains("GET")}?.toList()?.get(0).toString().split(" ")[1]
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
            .expectBody(CountryRegistrationResponseDTO::class.java)
            .consumeWith { response -> val countries = response.responseBody
                countryUrl =
                    countries?.links?.stream()?.filter{ country -> country.contains("DELETE")}?.toList()?.get(0).toString().split(" ")[1]
            }

        webTestClient
            .delete()
            .uri(countryUrl)
            .exchange()
            .expectStatus().isNoContent
    }
}