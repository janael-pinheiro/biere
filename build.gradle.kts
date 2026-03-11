plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.4.1"
	id("io.spring.dependency-management") version "1.1.7"
	id("org.flywaydb.flyway") version "9.22.1"
	kotlin("plugin.jpa") version "1.9.25"
	id("org.springdoc.openapi-gradle-plugin") version "1.9.0"
}

group = "com.biere"
version = "0.0.1-SNAPSHOT"
description = "Beer catalog"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

val postgresqlVersion = "42.7.8"

dependencies {
	// SpringBoot
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-hateoas")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("io.micrometer:micrometer-registry-prometheus:1.16.1")
	implementation("io.micrometer:micrometer-tracing-bridge-otel:1.6.1")
	implementation("io.opentelemetry:opentelemetry-exporter-otlp")
	implementation("com.github.loki4j:loki-logback-appender:2.0.2")

	// Kotlin/Jackson
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")

	// Driver JDBC
	implementation("org.postgresql:postgresql:${postgresqlVersion}")

	// JJWT e Lombok
	implementation("io.jsonwebtoken:jjwt-api:0.13.0")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.13.0")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.13.0")
	implementation("org.projectlombok:lombok:1.18.42")

	implementation("com.opencsv:opencsv:5.7.1")

	// Flyway
	implementation("org.flywaydb:flyway-core")
	implementation("org.flywaydb:flyway-database-postgresql")
	// Rate limiting
	implementation("com.bucket4j:bucket4j_jdk17-core:8.16.1")
	// OpenAPI
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.7.0")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.boot:spring-boot-starter-webflux")
	testImplementation("org.springframework.boot:spring-boot-testcontainers")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testImplementation("org.testcontainers:junit-jupiter")
	testImplementation("org.springframework.security:spring-security-test")
	testImplementation("org.postgresql:postgresql:${postgresqlVersion}")
	testImplementation("org.testcontainers:postgresql")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

allOpen {
	annotation("jakarta.persistence.Entity")
	annotation("jakarta.persistence.MappedSuperclass")
	annotation("jakarta.persistence.Embeddable")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
	systemProperty("api.version", "1.44")
}

flyway {
	url = "jdbc:postgresql://localhost:5432/biere"
	user = "admin"
	password = "password"
	locations = arrayOf("filesystem:src/main/resources/db/migration")
}

openApi {
	apiDocsUrl.set("http://localhost:8080/v3/api-docs")
	outputDir.set(projectDir)
	outputFileName.set("openapi.yaml")
	waitTimeInSeconds.set(60)
}

configurations.all {
	exclude(group = "commons-logging", module = "commons-logging")
}