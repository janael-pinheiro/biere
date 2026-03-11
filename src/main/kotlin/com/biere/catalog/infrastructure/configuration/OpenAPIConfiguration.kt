package com.biere.catalog.infrastructure.configuration

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.security.SecurityScheme
import org.springframework.context.annotation.Configuration

@Configuration
@OpenAPIDefinition(
    info = Info(
        title = "Biere Catalog API",
        version = "1.0",
        description = "API for managing beers, breweries, styles, and countries. Use 'X-Idempotency-Key' in POST/PUT/PATCH operations to prevent duplicate processing."
    ),
    security = [
        SecurityRequirement(name = "Bearer Authentication"),
        SecurityRequirement(name = "Idempotency Key")
    ]
)
@SecurityScheme(
    name = "Bearer Authentication",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer"
)
@SecurityScheme(
    name = "Idempotency Key",
    type = SecuritySchemeType.APIKEY,
    `in` = io.swagger.v3.oas.annotations.enums.SecuritySchemeIn.HEADER,
    paramName = "X-Idempotency-Key"
)
class OpenAPIConfiguration
