package com.biere.catalog.configuration

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import org.springframework.context.annotation.Configuration

@Configuration
@OpenAPIDefinition(
    info = Info(
        title = "Biere Catalog API",
        version = "1.0",
        description = "API for managing beers, breweries, styles, and countries."
    )
)
class OpenAPIConfiguration
