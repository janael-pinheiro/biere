package com.biere.catalog

import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest(classes = [com.biere.catalog.CatalogApplication::class])
@Testcontainers
class CatalogApplicationTests {

	@MockitoBean
	private lateinit var authenticationManager: AuthenticationManager

	@Test
	fun contextLoads() {
	}
}
