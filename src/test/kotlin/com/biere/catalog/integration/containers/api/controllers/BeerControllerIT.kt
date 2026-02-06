package com.biere.catalog.integration.containers.api.controllers

import com.biere.catalog.adapters.entities.BeerEntity
import com.biere.catalog.adapters.entities.BreweryEntity
import com.biere.catalog.adapters.entities.CountryEntity
import com.biere.catalog.adapters.entities.StyleEntity
import com.biere.catalog.adapters.output.repositories.BeerRepository
import com.biere.catalog.adapters.output.repositories.BreweryRepository
import com.biere.catalog.adapters.output.repositories.CountryRepository
import com.biere.catalog.adapters.output.repositories.StyleRepository
import com.biere.catalog.containers.api.dtos.ApiIndividualResponseDTO
import com.biere.catalog.containers.api.dtos.BeerRegistrationDTO
import com.biere.catalog.containers.api.dtos.BeerUpdateRequestDTO
import com.biere.catalog.integration.configuration.PostgresTestContainersConfiguration
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.core.io.ByteArrayResource
import org.springframework.hateoas.MediaTypes
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.ZonedDateTime
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull

@AutoConfigureWebTestClient
@Import(PostgresTestContainersConfiguration::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = [com.biere.catalog.containers.api.CatalogApplication::class])
@ActiveProfiles("test")
class BeerControllerIT(
    @Autowired private val webTestClient: WebTestClient,
    @Autowired private val beerRepository: BeerRepository,
    @Autowired private val breweryRepository: BreweryRepository,
    @Autowired private val countryRepository: CountryRepository,
    @Autowired private val styleRepository: StyleRepository) {

    private val beersUri = "/v1/beers"
    private lateinit var newBeer: BeerRegistrationDTO
    private lateinit var brewery: BreweryEntity
    private lateinit var style: StyleEntity

    @BeforeEach
    fun setup(){
        val country = CountryEntity(name="Netherlands", createdAt = ZonedDateTime.now())
        val savedCountry = this.countryRepository.save(country)
        brewery = this.breweryRepository.save(BreweryEntity(name = "Heineken", country = savedCountry))
        style = this.styleRepository.save(StyleEntity(name = "lager"))
        newBeer = BeerRegistrationDTO(name = "Heineken", alcoholContent = 4.5F, breweryId = brewery.id ?: 0, styleId = style.id ?: 0, year = 2024L)
    }

    @AfterEach
    fun tearDown(){
        beerRepository.deleteAll()
        breweryRepository.deleteAll()
        countryRepository.deleteAll()
        styleRepository.deleteAll()
    }

    @Test
    fun `register beer`(){
        webTestClient
            .post()
            .uri(beersUri)
            .header("Accept", MediaTypes.HAL_JSON_VALUE)
            .bodyValue(newBeer)
            .exchange()
            .expectStatus().isCreated
            .expectHeader().contentType(MediaTypes.HAL_JSON.toString())
            .expectBody()
            .jsonPath("$.data.name").isEqualTo(newBeer.name)
            .jsonPath("$._links.self.href").exists()
            .jsonPath("$._links.self.method").isEqualTo("GET")
            .jsonPath("$._links.update_beer.href").exists()
            .jsonPath("$._links.update_beer.method").isEqualTo("PATCH")
    }

    @Test
    fun `register beer, when blank name should return bad request`(){
        val newBeerWithoutName = newBeer.copy(name = "")
        webTestClient
            .post()
            .uri(beersUri)
            .header("Accept", MediaTypes.HAL_JSON_VALUE)
            .bodyValue(newBeerWithoutName)
            .exchange()
            .expectStatus().is4xxClientError
            .expectBody()
            .jsonPath("$.title").isEqualTo("Validation error")
            .jsonPath("$.invalid-params[0].reason").isEqualTo("The name of the beer cannot be empty")
    }

    @Test
    fun `register beer, when null alcohol content should return bad request`(){
        val newBeerWithoutName = newBeer.copy(alcoholContent = null)
        webTestClient
            .post()
            .uri(beersUri)
            .header("Accept", MediaTypes.HAL_JSON_VALUE)
            .bodyValue(newBeerWithoutName)
            .exchange()
            .expectStatus().is4xxClientError
            .expectBody()
            .jsonPath("$.title").isEqualTo("Validation error")
            .jsonPath("$.invalid-params[0].reason").isEqualTo("Alcohol content can't be null")
    }

    @Test
    fun `register beer, when alcohol content is greater than 100 percent should return bad request`(){
        val newBeerWithoutName = newBeer.copy(alcoholContent = 100.1F)
        webTestClient
            .post()
            .uri(beersUri)
            .header("Accept", MediaTypes.HAL_JSON_VALUE)
            .bodyValue(newBeerWithoutName)
            .exchange()
            .expectStatus().is4xxClientError
            .expectBody()
            .jsonPath("$.title").isEqualTo("Validation error")
            .jsonPath("$.invalid-params[0].reason").isEqualTo("Alcohol content can't be greater than 100%")
    }

    @Test
    fun `register beer, when null breweryId should return bad request`(){
        val newBeerWithoutName = newBeer.copy(breweryId = null)
        webTestClient
            .post()
            .uri(beersUri)
            .header("Accept", MediaTypes.HAL_JSON_VALUE)
            .bodyValue(newBeerWithoutName)
            .exchange()
            .expectStatus().is4xxClientError
            .expectBody()
            .jsonPath("$.title").isEqualTo("Validation error")
            .jsonPath("$.invalid-params[0].reason").isEqualTo("breweryId can't be null")
    }

    @Test
    fun `register beer, when null styleId should return bad request`(){
        val newBeerWithoutName = newBeer.copy(styleId = null)
        webTestClient
            .post()
            .uri(beersUri)
            .header("Accept", MediaTypes.HAL_JSON_VALUE)
            .bodyValue(newBeerWithoutName)
            .exchange()
            .expectStatus().is4xxClientError
            .expectBody()
            .jsonPath("$.title").isEqualTo("Validation error")
            .jsonPath("$.invalid-params[0].reason").isEqualTo("styleId can't be null")
    }

    @Test
    fun `register beer, when null year should return bad request`(){
        val newBeerWithoutName = newBeer.copy(year = null)
        webTestClient
            .post()
            .uri(beersUri)
            .header("Accept", MediaTypes.HAL_JSON_VALUE)
            .bodyValue(newBeerWithoutName)
            .exchange()
            .expectStatus().is4xxClientError
            .expectBody()
            .jsonPath("$.title").isEqualTo("Validation error")
            .jsonPath("$.invalid-params[0].reason").isEqualTo("year can't be null")
    }

    @Test
    fun `register beer, when already registered name should return conflict`(){
        webTestClient
            .post()
            .uri(beersUri)
            .header("Accept", MediaTypes.HAL_JSON_VALUE)
            .bodyValue(newBeer)
            .exchange()
            .expectStatus().isCreated
            .expectHeader().contentType(MediaTypes.HAL_JSON.toString())

        val duplicateBeerName = newBeer.copy()
        webTestClient
            .post()
            .uri(beersUri)
            .header("Accept", MediaTypes.HAL_JSON_VALUE)
            .bodyValue(duplicateBeerName)
            .exchange()
            .expectStatus().is4xxClientError
            .expectBody()
            .jsonPath("$.message").isEqualTo("Beer with name ${duplicateBeerName.name} already exists.")
    }

    @Test
    fun `register beer, when brewery does not exist return bad request`(){
        val newBeerWithInvalidBreweryId = newBeer.copy(breweryId = 42)
        webTestClient
            .post()
            .uri(beersUri)
            .header("Accept", MediaTypes.HAL_JSON_VALUE)
            .bodyValue(newBeerWithInvalidBreweryId)
            .exchange()
            .expectStatus().is4xxClientError
            .expectBody()
            .jsonPath("$.message").isEqualTo("Brewery with id ${newBeerWithInvalidBreweryId.breweryId} does not exist.")
    }

    @Test
    fun `register beer, when style does not exist return bad request`(){
        val newBeerWithInvalidStyleId = newBeer.copy(styleId = 42)
        webTestClient
            .post()
            .uri(beersUri)
            .header("Accept", MediaTypes.HAL_JSON_VALUE)
            .bodyValue(newBeerWithInvalidStyleId)
            .exchange()
            .expectStatus().is4xxClientError
            .expectBody()
            .jsonPath("$.message").isEqualTo("Style with id ${newBeerWithInvalidStyleId.styleId} does not exist.")
    }

    @Test
    fun `get a specific beer`(){
        var beerUrl = ""

        webTestClient
            .post()
            .uri(beersUri)
            .bodyValue(newBeer)
            .exchange()
            .expectStatus().isCreated
            .expectBody(Map::class.java)
            .consumeWith { response ->
                val links = response.responseBody?.get("_links") as Map<*, *>
                beerUrl = (links["self"] as Map<*, *>)["href"] as String
            }

        webTestClient
            .get()
            .uri(beerUrl)
            .exchange()
            .expectStatus().isOk
            .expectBody(ApiIndividualResponseDTO::class.java)
            .consumeWith { response -> val beer = response.responseBody
                assertNotNull(beer?.links)
            }
    }

    @Test
    fun `get multiple beers as JSON`(){
        this.registerBeers()
        webTestClient
            .get()
            .uri(beersUri)
            .header("Accept", MediaTypes.HAL_JSON_VALUE)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith { response -> val responseHeaders = response.responseHeaders
                assertEquals(MediaTypes.HAL_JSON_VALUE, responseHeaders["Content-Type"]?.get(0).toString())
            }
            .jsonPath("$._links.create_new_beer.href").exists()
            .jsonPath("$._links.get_all_breweries.href").exists()
            .jsonPath("$._links.get_all_countries.href").exists()
            .jsonPath("$._links.get_all_styles.href").exists()
    }

    @Test
    fun `get multiple beers as CSV`(){
        this.registerBeers()
        webTestClient
            .get()
            .uri(beersUri)
            .header("Accept", "text/csv")
            .exchange()
            .expectStatus().isOk
            .expectBody(ByteArrayResource::class.java)
            .consumeWith { response -> val responseHeaders = response.responseHeaders
                assertEquals("text/csv", responseHeaders.get("Content-Type")?.get(0).toString())
                assertEquals("attachment; filename=\"beers.csv\"", responseHeaders["Content-Disposition"]?.get(0).toString())
            }
    }

    @Test
    fun `update a beer`(){
        val newName = "test1"
        var updateBeerUrl = ""
        var getBeerUrl = ""

        webTestClient
            .post()
            .uri(beersUri)
            .bodyValue(newBeer)
            .exchange()
            .expectBody(Map::class.java)
            .consumeWith { response ->
                val links = response.responseBody?.get("_links") as Map<*, *>
                updateBeerUrl = (links["update_beer"] as Map<*, *>)["href"] as String
                getBeerUrl = (links["self"] as Map<*, *>)["href"] as String
            }

        webTestClient
            .patch()
            .uri(updateBeerUrl)
            .bodyValue(BeerUpdateRequestDTO(name = newName, alcoholContent = null, breweryId = null, styleId = null, year = 2025L))
            .exchange()
            .expectStatus().isOk

        webTestClient
            .get()
            .uri(getBeerUrl)
            .exchange()
            .expectStatus().isOk
            .expectBody(ApiIndividualResponseDTO::class.java)
            .consumeWith { response -> val beer = response.responseBody?.data as? Map<*, *>
                assertEquals(newName, beer?.get("name"))
            }
    }

    @Test
    fun `delete a specific beer`(){
        var beerUrl = ""

        webTestClient
            .post()
            .uri(beersUri)
            .bodyValue(newBeer)
            .exchange()
            .expectStatus().isCreated
            .expectBody(Map::class.java)
            .consumeWith { response ->
                val links = response.responseBody?.get("_links") as Map<*, *>
                beerUrl = (links["self"] as Map<*, *>)["href"] as String
            }

        webTestClient
            .delete()
            .uri(beerUrl)
            .exchange()
            .expectStatus().isNoContent

        val urlSequence = beerUrl.split("/")
        val existsBeer = this.beerRepository.existsById(urlSequence[urlSequence.size-1].toLong())
        assertFalse(existsBeer)
    }

    @Test
    fun `delete a specific beer, when beer not found should return 404`(){
        var beerUrl = ""

        webTestClient
            .post()
            .uri(beersUri)
            .bodyValue(newBeer)
            .exchange()
            .expectStatus().isCreated
            .expectBody(Map::class.java)
            .consumeWith { response ->
                val links = response.responseBody?.get("_links") as Map<*, *>
                beerUrl = (links["self"] as Map<*, *>)["href"] as String
            }

        val urlSequence = beerUrl.split("/").toMutableList()
        urlSequence[urlSequence.size-1] = "0"

        webTestClient
            .delete()
            .uri(urlSequence.joinToString("/"))
            .exchange()
            .expectStatus().isNotFound
    }

    private fun registerBeers(){
        this.beerRepository.save(BeerEntity(
            name = "Heineken",
            alcoholContent = 4.6F,
            brewery = this.brewery,
            style = this.style,
            year = 2024L))
        this.beerRepository.save(BeerEntity(
            name = "Amstel",
            alcoholContent = 4.6F,
            brewery = this.brewery,
            style = this.style,
            year = 2024L))
    }
}