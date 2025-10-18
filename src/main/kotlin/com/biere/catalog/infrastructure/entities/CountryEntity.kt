package com.biere.catalog.infrastructure.entities

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import lombok.Builder
import lombok.Getter
import lombok.NoArgsConstructor
import lombok.Setter
import java.time.ZonedDateTime

@Entity
@Table(name = "countries")
@Getter
@Setter
@Builder
class CountryEntity(@Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null, val name: String, private val createdAt: ZonedDateTime) {
}