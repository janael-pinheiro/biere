package com.biere.catalog.integration

import org.junit.jupiter.api.Test
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

class TestcontainersTest {
    @Test
    fun testDocker() {
        PostgreSQLContainer(DockerImageName.parse("postgres:13.16-bookworm")).use {
            it.start()
            println("Container started!")
        }
    }
}
