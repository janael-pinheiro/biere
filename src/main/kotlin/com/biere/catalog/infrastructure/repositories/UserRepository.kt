package com.biere.catalog.infrastructure.repositories

import com.biere.catalog.infrastructure.entities.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository: JpaRepository<UserEntity, Long>{
    fun existsByEmailAndPassword(email: String, password: String): Boolean
}