package com.biere.catalog.infrastructure.configuration

import com.biere.catalog.infrastructure.adapter.input.rest.controllers.Scopes
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.info.Contact
import io.swagger.v3.oas.annotations.security.OAuthFlow
import io.swagger.v3.oas.annotations.security.OAuthFlows
import io.swagger.v3.oas.annotations.security.OAuthScope
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.security.SecurityScheme
import io.swagger.v3.oas.annotations.security.SecuritySchemes
import org.springframework.context.annotation.Configuration

@Configuration
@OpenAPIDefinition(
    info = Info(
        title = "Biere Catalog API",
        version = "1.0",
        description = """
            API for managing beers, breweries, styles, and countries.

            ## Authentication
            All protected endpoints require a Bearer JWT token obtained via `POST /v1/users/login`.
            The token contains **scopes** that define what operations the caller is authorized to perform.

            ## Scopes
            | Scope | Description |
            |---|---|
            | `beers:read` | Read beer resources (list, get) |
            | `beers:write` | Create, update and delete beers |
            | `breweries:read` | Read brewery resources |
            | `breweries:write` | Create, update and delete breweries |
            | `countries:read` | Read country resources |
            | `countries:write` | Create, update and delete countries |
            | `styles:read` | Read style resources |
            | `styles:write` | Create, update and delete styles |

            ## Idempotency
            All state-changing operations (POST, PUT, PATCH, DELETE) **require** an `X-Idempotency-Key` header
            containing a unique UUID. Repeating a request with the same key returns the cached response
            without re-executing the operation.

            ## Rate Limiting
            Responses include `X-Rate-Limit-Limit` and `X-Rate-Limit-Remaining` headers.
            When the limit is exceeded, the API returns `429 Too Many Requests` with a `Retry-After` header.
        """,
        contact = Contact(name = "Biere API Support", url = "https://biere.catalog.com")
    ),
    security = [
        SecurityRequirement(name = "Bearer Authentication"),
        SecurityRequirement(name = "Idempotency Key")
    ]
)
@SecuritySchemes(
    SecurityScheme(
        name = "Bearer Authentication",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer",
        description = """
            JWT token obtained via `POST /v1/users/login`. The token payload contains a `scopes` claim
            listing the permissions granted to the caller. Use `Authorization: Bearer <token>` header.
        """
    ),
    SecurityScheme(
        name = "Idempotency Key",
        type = SecuritySchemeType.APIKEY,
        `in` = SecuritySchemeIn.HEADER,
        paramName = "X-Idempotency-Key",
        description = """
            A unique client-generated UUID (e.g. `uuid4()`) that identifies a specific request.
            Required for all state-changing operations. The server caches the response and replays it
            for any subsequent request with the same key, preventing duplicate side-effects.
        """
    ),
    SecurityScheme(
        name = "OAuth2 Scopes",
        type = SecuritySchemeType.OAUTH2,
        description = "Scopes embedded in the JWT token that control access to API resources.",
        flows = OAuthFlows(
            password = OAuthFlow(
                tokenUrl = "/v1/users/login",
                refreshUrl = "/v1/users/refresh-token",
                scopes = [
                    OAuthScope(name = Scopes.BEER_READ,     description = "Read beer resources (list and get)"),
                    OAuthScope(name = Scopes.BEER_WRITE,    description = "Create, update and delete beers"),
                    OAuthScope(name = Scopes.BREWERY_READ,  description = "Read brewery resources"),
                    OAuthScope(name = Scopes.BREWERY_WRITE, description = "Create, update and delete breweries"),
                    OAuthScope(name = Scopes.COUNTRY_READ,  description = "Read country resources"),
                    OAuthScope(name = Scopes.COUNTRY_WRITE, description = "Create, update and delete countries"),
                    OAuthScope(name = Scopes.STYLE_READ,    description = "Read style resources"),
                    OAuthScope(name = Scopes.STYLE_WRITE,   description = "Create, update and delete styles"),
                    OAuthScope(name = Scopes.USER_WRITE,    description = "Register and remove user accounts")
                ]
            )
        )
    )
)
class OpenAPIConfiguration
