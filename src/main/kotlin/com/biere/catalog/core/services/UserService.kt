package com.biere.catalog.core.services

import com.biere.catalog.adapters.entities.UserEntity
import com.biere.catalog.adapters.output.repositories.UserRepository
import com.biere.catalog.containers.api.controllers.user.TokenRequestDTO
import com.biere.catalog.containers.api.controllers.user.TokenResponseDTO
import com.biere.catalog.core.exceptions.NotAuthorizedException
import com.biere.catalog.core.models.InputUser
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.io.Encoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.security.Key
import java.util.*

@Service
class UserService(
    @Value("\${jwt.secret}") private val secret: String,
    @Value("\${jwt.expiration}") private val expiration: Long,
    val userRepository: UserRepository
) {
    private val signingKey: Key by lazy {
        Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret))
    }

    fun generateToken(loginRequest: TokenRequestDTO): TokenResponseDTO {
        if(!isUserValid(loginRequest.email, loginRequest.password)){
            throw NotAuthorizedException("E-mail or password incorrect.")
        }
        val now = Date()
        val expiryDate = Date(now.time + expiration)

        val jwtToken = Jwts.builder()
            .subject(loginRequest.email)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(signingKey)
            .compact()
        return TokenResponseDTO(token = jwtToken)
    }

    private fun isUserValid(email: String, password: String): Boolean {
        return userRepository.existsByEmailAndPassword(email, password)
    }

  fun isTokenValid(token: String): Boolean {
        return try {
            parseToken(token)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getEmailFromToken(token: String): String {
        val parsedToken = parseToken(token)
        return parsedToken.payload.subject
    }

    private fun parseToken(token: String): Jws<Claims> {
        return Jwts.parser()
            .verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret)))
            .build()
            .parseSignedClaims(token)
    }

    fun registerUser(user: InputUser): Long {
        val encodedPassword = Encoders.BASE64.encode(user.toString().toByteArray(Charsets.UTF_8))
        return userRepository.save(UserEntity(name = user.name, email = user.email, password = encodedPassword)).id ?:0
    }

    fun removeUser(userId: Long) {
        userRepository.deleteById(userId)
    }
}