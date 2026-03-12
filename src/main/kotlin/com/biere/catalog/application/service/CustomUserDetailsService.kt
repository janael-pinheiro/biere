package com.biere.catalog.application.service

import com.biere.catalog.domain.model.User
import com.biere.catalog.domain.port.output.UserOutputPort
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(private val userOutputPort: UserOutputPort) : UserDetailsService {

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(email: String): UserDetails {
        val user = userOutputPort.findByEmail(email).orElseThrow{ UsernameNotFoundException(email) }
        val authorities = mutableListOf(SimpleGrantedAuthority("ROLE_USER"))
        user.roles.forEach { role ->
            role.scopes.forEach { scope ->
                authorities.add(SimpleGrantedAuthority("SCOPE_${scope.name}"))
            }
        }
        val outputUser = User(
            user.id!!, user.name, user.email, user.password, authorities)
        return outputUser
    }
}
