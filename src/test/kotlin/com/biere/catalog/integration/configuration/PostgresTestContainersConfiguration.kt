package com.biere.catalog.integration.configuration

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.utility.DockerImageName

@TestConfiguration(proxyBeanMethods = false)
class PostgresTestContainersConfiguration {

    companion object {
        @ServiceConnection
        @Container
        @JvmStatic
        val postgresContainer = PostgreSQLContainer(DockerImageName.parse("postgres:latest"))
    }
    @Bean
    fun postgresContainer(): PostgreSQLContainer<*> {

        return postgresContainer
    }
}