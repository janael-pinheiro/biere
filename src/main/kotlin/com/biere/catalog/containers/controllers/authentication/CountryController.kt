package com.biere.catalog.containers.controllers.authentication

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping("/v1/countries")
class CountryController {
    @PostMapping
    fun registerCountry(): ResponseEntity<String> {
        return ResponseEntity.created(URI("")).build();
    }

    @GetMapping
    fun getCountries(): ResponseEntity<List<String>> {
        return ResponseEntity.ok(listOf("USA", "Germany"))
    }
}