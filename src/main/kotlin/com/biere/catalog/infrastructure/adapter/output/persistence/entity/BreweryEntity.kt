package com.biere.catalog.infrastructure.adapter.output.persistence.entity

import jakarta.persistence.*

@Entity
@Table(name = "breweries")
data class BreweryEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null,
    var name: String,
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "country_id") var country: CountryEntity)
