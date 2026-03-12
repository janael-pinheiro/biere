package com.biere.catalog.domain.port.output

import com.biere.catalog.domain.model.InputUser
import com.biere.catalog.infrastructure.adapter.output.persistence.entity.UserEntity
import java.util.Optional

interface UserOutputPort {
    fun existsByEmailAndPassword(email: String, passwordHash: String): Boolean
    fun save(user: InputUser, passwordHash: String): Long
    fun deleteById(userId: Long)
    fun findByEmail(email: String): Optional<UserEntity>
}
