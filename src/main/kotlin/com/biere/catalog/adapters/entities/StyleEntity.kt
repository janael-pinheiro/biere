package com.biere.catalog.adapters.entities

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "styles")
class StyleEntity(@Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null, val name: String) {
}