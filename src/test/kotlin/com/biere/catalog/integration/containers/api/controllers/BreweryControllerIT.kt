package com.biere.catalog.integration.containers.api.controllers

import com.biere.catalog.adapters.entities.BreweryEntity
import com.biere.catalog.integration.configuration.PostgresTestContainersConfiguration
import com.biere.catalog.containers.api.dtos.BreweryRegistrationDTO
import com.biere.catalog.adapters.entities.CountryEntity
import com.biere.catalog.adapters.output.repositories.BreweryRepository
import com.biere.catalog.adapters.output.repositories.CountryRepository
import com.biere.catalog.containers.api.dtos.ApiIndividualResponseDTO
import com.biere.catalog.containers.api.dtos.BreweryUpdateRequestDTO
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.hateoas.MediaTypes
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.ZonedDateTime
import kotlin.collections.get
import kotlin.test.assertEquals
import kotlin.test.assertFalse

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
    fun `register brewery, when blank name should return bad request`(){
        val invalidBrewery = this.newBrewery.copy(name = "")
        webTestClient
            .post()
            .uri(breweriesUri)
            .bodyValue(invalidBrewery)
            .exchange()
            .expectStatus().is4xxClientError
            .expectBody()
            .jsonPath("$.title").isEqualTo("Validation error")
            .jsonPath("$.invalid-params[0].reason").isEqualTo("The name of the brewery cannot be empty")
    }

    @Test
    fun `register brewery, when null countryId should return bad request`(){
        val invalidBrewery = this.newBrewery.copy(countryId = null)
        webTestClient
            .post()
            .uri(breweriesUri)
            .bodyValue(invalidBrewery)
            .exchange()
            .expectStatus().is4xxClientError
            .expectBody()
            .jsonPath("$.title").isEqualTo("Validation error")
            .jsonPath("$.invalid-params[0].reason").isEqualTo("CountryId can't be null")
    }

    @Test
    fun `register brewery, when already registered name should return conflict`(){
        webTestClient
            .post()
            .uri(breweriesUri)
            .header("Accept", MediaTypes.HAL_JSON_VALUE)
            .bodyValue(newBrewery)
            .exchange()
            .expectStatus().isCreated
            .expectHeader().contentType(MediaTypes.HAL_JSON.toString())

        val duplicateBreweryName = newBrewery.copy()
        webTestClient
            .post()
            .uri(breweriesUri)
            .header("Accept", MediaTypes.HAL_JSON_VALUE)
            .bodyValue(duplicateBreweryName)
            .exchange()
            .expectStatus().is4xxClientError
            .expectBody()
            .jsonPath("$.message").isEqualTo("The name ${newBrewery.name} already exists.")
    }

    @Test
    fun `register brewery, when country does not exist return bad request`(){
        val newBreweryWithInvalidBreweryId = newBrewery.copy(countryId = 42)
        webTestClient
            .post()
            .uri(breweriesUri)
            .header("Accept", MediaTypes.HAL_JSON_VALUE)
            .bodyValue(newBreweryWithInvalidBreweryId)
            .exchange()
            .expectStatus().is4xxClientError
            .expectBody()
            .jsonPath("$.message").isEqualTo("Country ${newBreweryWithInvalidBreweryId.countryId} not found.")
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

    @Test
    fun `get multiple breweries`(){
        this.registerBreweries()
        webTestClient
            .get()
            .uri(breweriesUri)
            .header("Accept", MediaTypes.HAL_JSON_VALUE)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith { response -> val responseHeaders = response.responseHeaders
                assertEquals(MediaTypes.HAL_JSON_VALUE, responseHeaders["Content-Type"]?.get(0).toString())
            }
            .jsonPath("$._links.create_new_brewery.href").exists()
    }

    @Test
    fun `update a brewery`(){
        val belgium = CountryEntity(name="Belgium", createdAt = ZonedDateTime.now())
        val savedBelgium = this.countryRepository.save(belgium)
        val newName = "test1"

        var updateBreweryUrl = ""
        var getBreweryUrl = ""

        webTestClient
            .post()
            .uri(breweriesUri)
            .bodyValue(newBrewery)
            .exchange()
            .expectBody(Map::class.java)
            .consumeWith { response ->
                val links = response.responseBody?.get("_links") as Map<*, *>
                updateBreweryUrl = (links["update_brewery"] as Map<*, *>)["href"] as String
                getBreweryUrl = (links["self"] as Map<*, *>)["href"] as String
            }

        webTestClient
            .put()
            .uri(updateBreweryUrl)
            .bodyValue(BreweryUpdateRequestDTO(name = newName, countryId = savedBelgium.id!!))
            .exchange()
            .expectStatus().isOk

        webTestClient
            .get()
            .uri(getBreweryUrl)
            .exchange()
            .expectStatus().isOk
            .expectBody(ApiIndividualResponseDTO::class.java)
            .consumeWith { response -> val brewery = response.responseBody?.data as? Map<*, *>
                assertEquals(newName, brewery?.get("name"))
            }
    }

    @Test
    fun `update a brewery, when country does not exist return not found`(){
        val newName = "test1"
        var updateBreweryUrl = ""

        webTestClient
            .post()
            .uri(breweriesUri)
            .bodyValue(newBrewery)
            .exchange()
            .expectBody(Map::class.java)
            .consumeWith { response ->
                val links = response.responseBody?.get("_links") as Map<*, *>
                updateBreweryUrl = (links["update_brewery"] as Map<*, *>)["href"] as String
            }

        webTestClient
            .put()
            .uri(updateBreweryUrl)
            .bodyValue(BreweryUpdateRequestDTO(name = newName, countryId = 42))
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun `update a brewery, when brewery does not exist return not found`(){
        val newName = "test1"
        var updateBreweryUrl = ""

        webTestClient
            .post()
            .uri(breweriesUri)
            .bodyValue(newBrewery)
            .exchange()
            .expectBody(Map::class.java)
            .consumeWith { response ->
                val links = response.responseBody?.get("_links") as Map<*, *>
                updateBreweryUrl = (links["update_brewery"] as Map<*, *>)["href"] as String
            }

        val urlSequence = updateBreweryUrl.split("/").toMutableList()
        urlSequence[urlSequence.size-1] = "0"

        webTestClient
            .put()
            .uri(urlSequence.joinToString("/"))
            .bodyValue(BreweryUpdateRequestDTO(name = newName, countryId = newBrewery.countryId!!))
            .exchange()
            .expectStatus().isNotFound
            .expectBody()
            .jsonPath("$.message").isEqualTo("Brewery not found.")
    }

    @Test
    fun `delete a specific brewery`(){
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
            .delete()
            .uri(breweryUrl)
            .exchange()
            .expectStatus().isNoContent

        val urlSequence = breweryUrl.split("/")
        val existsBrewery = this.breweryRepository.existsById(urlSequence[urlSequence.size-1].toLong())
        assertFalse(existsBrewery)
    }

    private fun registerBreweries(){
        val netherlands = CountryEntity(name = "Netherlands", createdAt = ZonedDateTime.now())
        val usa = CountryEntity(name = "USA", createdAt = ZonedDateTime.now())
        val savedNetherlands = this.countryRepository.saveAndFlush(netherlands)
        val savedUsa = this.countryRepository.saveAndFlush(usa)
        this.breweryRepository.save(BreweryEntity(name = "Heineken", country = savedNetherlands))
        this.breweryRepository.save(BreweryEntity(name = "AB Inbev", country = savedUsa))
    }
}