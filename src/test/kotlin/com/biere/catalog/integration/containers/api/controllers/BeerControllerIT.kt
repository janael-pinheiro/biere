package com.biere.catalog.integration.containers.api.controllers

import com.biere.catalog.adapters.entities.BeerEntity
import com.biere.catalog.integration.configuration.PostgresTestContainersConfiguration
import com.biere.catalog.containers.api.dtos.BeerRegistrationDTO
import com.biere.catalog.containers.api.dtos.BeerResponseDTO
import com.biere.catalog.containers.api.dtos.BeerUpdateRequestDTO
import com.biere.catalog.adapters.entities.BreweryEntity
import com.biere.catalog.adapters.entities.CountryEntity
import com.biere.catalog.adapters.entities.StyleEntity
import com.biere.catalog.adapters.output.repositories.BeerRepository
import com.biere.catalog.adapters.output.repositories.BreweryRepository
import com.biere.catalog.adapters.output.repositories.CountryRepository
import com.biere.catalog.adapters.output.repositories.StyleRepository
import com.biere.catalog.containers.api.dtos.ApiCollectionResponseDTO
import com.biere.catalog.containers.api.dtos.ApiGeneralRegistrationResponseDTO
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.core.io.ByteArrayResource
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.ZonedDateTime
import kotlin.test.assertEquals

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
        newBeer = BeerRegistrationDTO(name = "Heineken", countryId = savedCountry.id ?: 0, alcoholContent = 4.5F, breweryId = brewery.id ?: 0, styleId = style.id ?: 0, year = 2024L)
    }

    @AfterEach
    fun tearDown(){
        beerRepository.deleteAll()
        breweryRepository.deleteAll()
        countryRepository.deleteAll()
    }

    @Test
    fun `register beer`(){
        webTestClient
            .post()
            .uri(beersUri)
            .bodyValue(newBeer)
            .exchange()
            .expectStatus().isCreated
            .expectBody(ApiGeneralRegistrationResponseDTO::class.java)
            .consumeWith { response -> val beer = response.responseBody
                assertEquals(2, beer?.links?.toList()?.size)
            }
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
            .expectBody(ApiGeneralRegistrationResponseDTO::class.java)
            .consumeWith { response -> val beers = response.responseBody
                beerUrl =
                    beers?.links?.filter { link -> link.toString().contains("GET") }?.get(0).toString().split(" ")[1]
            }

        webTestClient
            .get()
            .uri(beerUrl)
            .exchange()
            .expectStatus().isOk
            .expectBody(BeerResponseDTO::class.java)
            .consumeWith { response -> val beer = response.responseBody
                assertEquals(newBeer.name, beer?.name)
            }
    }

    @Test
    fun `get multiple beers as JSON`(){
        this.registerBeers()
        webTestClient
            .get()
            .uri(beersUri)
            .header("Accept", "application/json")
            .exchange()
            .expectStatus().isOk
            .expectBody(ApiCollectionResponseDTO::class.java)
            .consumeWith { response -> val beers = response.responseBody
                assertEquals(2, (beers?.data as List<*>).size )
            }
            .consumeWith { response -> val responseHeaders = response.responseHeaders
                assertEquals("application/json", responseHeaders.get("Content-Type")?.get(0).toString())
            }
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
                assertEquals("attachment; filename=\"beers.csv\"", responseHeaders.get("Content-Disposition")?.get(0).toString())
            }
    }

    @Test
    fun `update a beer`(){
        val country = CountryEntity(name="Belgium", createdAt = ZonedDateTime.now())
        val savedCountry = this.countryRepository.save(country)

        var beerUrl = ""

        webTestClient
            .post()
            .uri(beersUri)
            .bodyValue(newBeer)
            .exchange()
            .expectStatus().isCreated
            .expectBody(ApiGeneralRegistrationResponseDTO::class.java)
            .consumeWith { response -> val beers = response.responseBody
                beerUrl =
                    beers?.links?.filter { link -> link.toString().contains("PATCH") }?.get(0).toString().split(" ")[1]
            }

        webTestClient
            .patch()
            .uri(beerUrl)
            .bodyValue(BeerUpdateRequestDTO(name = null, alcoholContent = null, breweryId = null, styleId = null, year = 2025L))
            .exchange()
            .expectStatus().isOk

        webTestClient
            .get()
            .uri(beerUrl)
            .exchange()
            .expectStatus().isOk
            .expectBody(BeerResponseDTO::class.java)
            .consumeWith { response -> val beer = response.responseBody
                assertEquals(newBeer.name, beer?.name)
                assertEquals(savedCountry.name, beer?.countryName)
            }
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