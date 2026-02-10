package com.biere.catalog.domain.model

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

data class User(
    val id: Long,
    val name: String,
    private val userEmail: String,
    private val userPassword: String,
    private val authorities: Collection<GrantedAuthority>
) : UserDetails {

    override fun getUsername(): String = name
    override fun getPassword(): String = userPassword
    override fun getAuthorities(): Collection<GrantedAuthority> = authorities
    override fun isAccountNonExpired(): Boolean = true
    override fun isAccountNonLocked(): Boolean = true
    override fun isCredentialsNonExpired(): Boolean = true
    override fun isEnabled(): Boolean = true
}