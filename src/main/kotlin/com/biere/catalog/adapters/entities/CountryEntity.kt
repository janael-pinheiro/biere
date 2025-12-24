package com.biere.catalog.adapters.entities

import jakarta.persistence.*
import lombok.Builder
import lombok.Getter
import lombok.Setter
import java.time.ZonedDateTime

@Entity
@Table(name = "countries")
@Getter
@Setter
@Builder
class CountryEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null,
    var name: String,
    private val createdAt: ZonedDateTime) {
}