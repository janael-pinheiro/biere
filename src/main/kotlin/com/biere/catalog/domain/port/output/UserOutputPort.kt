package com.biere.catalog.domain.port.output

import com.biere.catalog.domain.model.InputUser

interface UserOutputPort {
    fun existsByEmailAndPassword(email: String, passwordHash: String): Boolean
    fun save(user: InputUser, passwordHash: String): Long
    fun deleteById(userId: Long)
}
