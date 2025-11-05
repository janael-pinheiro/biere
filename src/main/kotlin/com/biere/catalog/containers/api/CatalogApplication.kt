package com.biere.catalog.containers.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@EntityScan(basePackages = ["com.biere.catalog.adapters.entities"])
@EnableJpaRepositories(basePackages = ["com.biere.catalog.adapters.output.repositories"])
@SpringBootApplication(scanBasePackages = ["com.biere.catalog"])
class CatalogApplication

fun main(args: Array<String>) {
	runApplication<CatalogApplication>(*args)
}
