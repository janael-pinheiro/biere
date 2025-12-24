package com.biere.catalog.adapters.entities

import jakarta.persistence.*

@Entity
@Table(name = "users")
class UserEntity(@Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null, val name: String, val email: String, val password: String) {
}