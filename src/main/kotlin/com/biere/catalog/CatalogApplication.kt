package com.biere.catalog

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@EntityScan(basePackages = ["com.biere.catalog.infrastructure.adapter.output.persistence.entity"])
@EnableJpaRepositories(basePackages = ["com.biere.catalog.infrastructure.adapter.output.persistence.repository"])
@SpringBootApplication(scanBasePackages = ["com.biere.catalog"])
class CatalogApplication

fun main(args: Array<String>) {
	runApplication<CatalogApplication>(*args)
}
