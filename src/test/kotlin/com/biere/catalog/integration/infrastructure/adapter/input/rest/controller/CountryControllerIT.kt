package com.biere.catalog.integration.infrastructure.adapter.input.rest.controller

import com.biere.catalog.infrastructure.adapter.output.persistence.entity.CountryEntity
import com.biere.catalog.infrastructure.adapter.output.persistence.repository.CountryRepository
import com.biere.catalog.infrastructure.adapter.input.rest.dtos.ApiCollectionResponseDTO
import com.biere.catalog.infrastructure.adapter.input.rest.dtos.ApiIndividualResponseDTO
import com.biere.catalog.infrastructure.adapter.input.rest.dtos.CountryRegistrationDTO
import com.biere.catalog.infrastructure.adapter.input.rest.dtos.CountryUpdateRequestDTO
import com.biere.catalog.integration.configuration.PostgresTestContainersConfiguration
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.ZonedDateTime
import kotlin.collections.get
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@AutoConfigureWebTestClient
@Import(PostgresTestContainersConfiguration::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = [com.biere.catalog.CatalogApplication::class])
@ActiveProfiles("test")
class CountryControllerIT(@Autowired var webTestClient: WebTestClient, @Autowired val countryRepository: CountryRepository) {
    private val newCountryName = "Netherlands"
    private val countriesUri = "/v1/countries"

    @AfterEach
    fun tearDown(){
        countryRepository.deleteAll()
    }

    @Test
    fun `register country`(){
        webTestClient
            .post()
            .uri(countriesUri)
            .bodyValue(CountryRegistrationDTO(name = newCountryName))
            .exchange()
            .expectStatus().isCreated
            .expectBody()
            .jsonPath("$.data.name").isEqualTo(newCountryName)
            .jsonPath("$._links.self.href").exists()
            .jsonPath("$._links.update_country.href").exists()
            .jsonPath("$._links.delete_country.href").exists()
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
    fun `get a specific country`(){
        var countryUrl = ""

        webTestClient
            .post()
            .uri(countriesUri)
            .bodyValue(CountryRegistrationDTO(newCountryName))
            .exchange()
            .expectStatus().isCreated
            .expectBody(Map::class.java)
            .consumeWith { response ->
                val links = response.responseBody?.get("_links") as Map<*, *>
                countryUrl = (links["self"] as Map<*, *>)["href"] as String
            }

        webTestClient
            .get()
            .uri(countryUrl)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.data.name").isEqualTo(newCountryName)
            .jsonPath("$._links.self.href").exists()
    }

    @Test
    fun `update a country`(){
        val newName = "test1"
        var updateCountryUrl = ""
        var getCountryUrl = ""

        webTestClient
            .post()
            .uri(countriesUri)
            .bodyValue(CountryRegistrationDTO(name = newCountryName))
            .exchange()
            .expectBody(Map::class.java)
            .consumeWith { response ->
                val links = response.responseBody?.get("_links") as Map<*, *>
                updateCountryUrl = (links["update_country"] as Map<*, *>)["href"] as String
                getCountryUrl = (links["self"] as Map<*, *>)["href"] as String
            }

        webTestClient
            .put()
            .uri(updateCountryUrl)
            .bodyValue(CountryUpdateRequestDTO(name = newName))
            .exchange()
            .expectStatus().isOk

        webTestClient
            .get()
            .uri(getCountryUrl)
            .exchange()
            .expectStatus().isOk
            .expectBody(ApiIndividualResponseDTO::class.java)
            .consumeWith { response -> val country = response.responseBody?.data as? Map<*, *>
                assertEquals(newName, country?.get("name"))
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
            .expectBody(Map::class.java)
            .consumeWith { response ->
                val links = response.responseBody?.get("_links") as Map<*, *>
                countryUrl = (links["self"] as Map<*, *>)["href"] as String
            }

        webTestClient
            .delete()
            .uri(countryUrl)
            .exchange()
            .expectStatus().isNoContent
    }
}