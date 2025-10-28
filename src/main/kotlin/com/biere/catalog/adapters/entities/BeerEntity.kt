package com.biere.catalog.adapters.entities

import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "beers")
class BeerEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null,
    val name: String,
    val alcoholContent: Float,
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "brewery_id") val brewery: BreweryEntity,
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "style_id") val style: StyleEntity)
