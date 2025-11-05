package com.biere.catalog.adapters.output.repositories

import com.biere.catalog.adapters.entities.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository: JpaRepository<UserEntity, Long>{
    fun existsByEmailAndPassword(email: String, password: String): Boolean
}