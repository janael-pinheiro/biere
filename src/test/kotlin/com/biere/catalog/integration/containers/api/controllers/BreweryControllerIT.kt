package com.biere.catalog.integration.containers.api.controllers

import com.biere.catalog.integration.configuration.PostgresTestContainersConfiguration
import com.biere.catalog.containers.api.dtos.BreweryRegistrationDTO
import com.biere.catalog.containers.api.dtos.BreweryResponseDTO
import com.biere.catalog.adapters.entities.CountryEntity
import com.biere.catalog.adapters.output.repositories.BreweryRepository
import com.biere.catalog.adapters.output.repositories.CountryRepository
import com.biere.catalog.containers.api.dtos.ApiIndividualResponseDTO
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
            .expectBody()
            .jsonPath("$._links.self.href").exists()
            .jsonPath("$._links.update_brewery.href").exists()
            .jsonPath("$._links.delete_brewery.href").exists()
            .jsonPath("$._links.get_all_breweries.href").exists()
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
            .expectBody(Map::class.java)
            .consumeWith { response ->
                val links = response.responseBody?.get("_links") as Map<*, *>
                breweryUrl = (links["self"] as Map<*, *>)["href"] as String
            }

        webTestClient
            .get()
            .uri(breweryUrl)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.data.name").isEqualTo(newBrewery.name)
            .jsonPath("$._links.self.href").exists()
    }
}