package com.biere.catalog.adapters.entities

import jakarta.persistence.*

@Entity
@Table(name = "beers")
class BeerEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null,
    var name: String,
    var alcoholContent: Float,
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "brewery_id") var brewery: BreweryEntity,
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "style_id") val style: StyleEntity,
    var year: Long)
