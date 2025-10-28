package com.biere.catalog.integration.containers.api.controllers

import com.biere.catalog.integration.configuration.PostgresTestContainersConfiguration
import com.biere.catalog.containers.api.dtos.BreweryRegistrationDTO
import com.biere.catalog.containers.api.dtos.BreweryResponseDTO
import com.biere.catalog.adapters.entities.CountryEntity
import com.biere.catalog.adapters.repositories.BreweryRepository
import com.biere.catalog.adapters.repositories.CountryRepository
import com.biere.catalog.containers.api.dtos.ApiGeneralRegistrationResponseDTO
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.ZonedDateTime
import kotlin.test.assertEquals

@AutoConfigureWebTestClient
@Import(PostgresTestContainersConfiguration::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = [com.biere.catalog.containers.api.CatalogApplication::class])
@ActiveProfiles("test")
class BreweryControllerIT(
    @Autowired private val webTestClient: WebTestClient,
    @Autowired private val breweryRepository: BreweryRepository,
    @Autowired private val countryRepository: CountryRepository) {

    private val breweriesUri = "/v1/breweries"
    private lateinit var newBrewery: BreweryRegistrationDTO

    @BeforeEach
    fun setup(){
        val country = CountryEntity(name="Netherlands", createdAt = ZonedDateTime.now())
        val savedCountry = this.countryRepository.save(country)
        newBrewery = BreweryRegistrationDTO(name = "Heineken", countryId = savedCountry.id ?: 0)
    }

    @AfterEach
    fun tearDown(){
        breweryRepository.deleteAll()
        countryRepository.deleteAll()
    }

    @Test
    fun `register brewery`(){
        webTestClient
            .post()
            .uri(breweriesUri)
            .bodyValue(newBrewery)
            .exchange()
            .expectStatus().isCreated
            .expectBody(ApiGeneralRegistrationResponseDTO::class.java)
            .consumeWith { response -> val brewery = response.responseBody
                assertEquals(1, brewery?.links?.size)
            }
    }

    @Test
    fun `get a specific brewery`(){
        var breweryUrl = ""

        webTestClient
            .post()
            .uri(breweriesUri)
            .bodyValue(newBrewery)
            .exchange()
            .expectStatus().isCreated
            .expectBody(ApiGeneralRegistrationResponseDTO::class.java)
            .consumeWith { response -> val breweryResponse = response.responseBody
                breweryUrl =
                    breweryResponse?.links?.stream()?.filter { brewery -> brewery.contains("GET") }?.toList()?.get(0).toString().split(" ")[1]
            }

        webTestClient
            .get()
            .uri(breweryUrl)
            .exchange()
            .expectStatus().isOk
            .expectBody(BreweryResponseDTO::class.java)
            .consumeWith { response -> val brewery = response.responseBody
                assertEquals(newBrewery.name, brewery?.name)
            }
    }
}