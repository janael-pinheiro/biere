package com.biere.catalog.containers.controllers.authentication

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping("/v1/beers")
class BeerController {
    @PostMapping
    fun register(): ResponseEntity<String> {
        return ResponseEntity.created(URI("")).build();
    }
}