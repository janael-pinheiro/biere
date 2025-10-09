package com.biere.catalog.core.services

import com.biere.catalog.core.models.User
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService() : UserDetailsService {

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails {
        val user = User(
            1L, "janael", "janael@example.com", BCryptPasswordEncoder().encode("fsdfs"), listOf(SimpleGrantedAuthority("ROLE_USER")))
        return user
    }
}
