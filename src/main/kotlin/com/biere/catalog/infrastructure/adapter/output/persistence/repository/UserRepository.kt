package com.biere.catalog.infrastructure.adapter.output.persistence.repository

import com.biere.catalog.infrastructure.adapter.output.persistence.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface UserRepository: JpaRepository<UserEntity, Long>{
    fun existsByEmailAndPassword(email: String, password: String): Boolean
    fun findByEmail(email: String): Optional<UserEntity>
}