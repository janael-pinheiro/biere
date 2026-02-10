package com.biere.catalog.infrastructure.adapter.output.persistence.entity

import jakarta.persistence.*

@Entity
@Table(name = "styles")
class StyleEntity(@Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null, var name: String) {
}