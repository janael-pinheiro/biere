package com.biere.catalog.infrastructure.adapter.output.persistence.repository

import com.biere.catalog.domain.model.InputUser
import com.biere.catalog.domain.port.output.UserOutputPort
import com.biere.catalog.infrastructure.adapter.output.persistence.entity.UserEntity
import org.springframework.stereotype.Component
import java.util.Optional

@Component
class UserOutputAdapter(
    private val userRepository: UserRepository
) : UserOutputPort {

    override fun existsByEmailAndPassword(email: String, passwordHash: String): Boolean {
        return userRepository.existsByEmailAndPassword(email, passwordHash)
    }

    override fun save(user: InputUser, passwordHash: String): Long {
        val userEntity = UserEntity(name = user.name, email = user.email, password = passwordHash)
        return userRepository.save(userEntity).id ?: 0
    }

    override fun deleteById(userId: Long) {
        userRepository.deleteById(userId)
    }

    override fun findByEmail(email: String): Optional<UserEntity> {
        return userRepository.findByEmail(email)
    }
}
