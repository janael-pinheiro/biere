package com.biere.catalog.infrastructure.configuration.annotations

import java.lang.annotation.Inherited

/**
 * Marks an endpoint as deprecated for AI agents and humans.
 * @property sunset The date when the endpoint will be removed (ISO-8601 or HTTP-date).
 * @property successor The URL of the successor endpoint.
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Inherited
annotation class DeprecatedEndpoint(
    val sunset: String = "",
    val successor: String = ""
)
