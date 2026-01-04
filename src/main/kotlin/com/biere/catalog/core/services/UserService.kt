package com.biere.catalog.core.services

import com.biere.catalog.adapters.entities.UserEntity
import com.biere.catalog.adapters.output.repositories.UserRepository
import com.biere.catalog.containers.api.dtos.TokenRequestDTO
import com.biere.catalog.containers.api.dtos.TokenResponseDTO
import com.biere.catalog.core.exceptions.ExpiredTokenException
import com.biere.catalog.core.exceptions.InvalidTokenException
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
import org.springframework.util.DigestUtils
import java.nio.charset.Charset
import java.security.Key
import java.security.MessageDigest
import java.util.*

@Service
class UserService(
    @Value("\${jwt.secret}") private val secret: String,
    @Value("\${access-token.expiration}") private val accessTokenExpiration: Long,
    @Value("\${refresh-token.expiration}") private val refreshTokenExpiration: Long,
    val userRepository: UserRepository
) {
    private val signingKey: Key by lazy {
        Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret))
    }

    fun generateToken(tokenRequest: TokenRequestDTO): TokenResponseDTO {
        if(!isUserValid(tokenRequest.email, tokenRequest.password)){
            throw NotAuthorizedException("E-mail or password incorrect.")
        }
        val now = Date()
        val accessToken = createToken(tokenRequest.email, now.time + accessTokenExpiration, now)
        val refreshToken = createToken(tokenRequest.email, now.time + refreshTokenExpiration, now)
        return TokenResponseDTO(accessToken = accessToken, refreshToken = refreshToken)
    }

    fun refreshToken(refreshToken: String): TokenResponseDTO {
        val now = Date()
        val accessToken = createToken("", now.time + accessTokenExpiration, now)
        val refreshToken = createToken("", now.time + refreshTokenExpiration, now)
        return TokenResponseDTO(accessToken = accessToken, refreshToken = refreshToken)
    }

    private fun createToken(email: String, expiration: Long, issueAt: Date): String {
        val jwtToken = Jwts.builder()
            .subject(email)
            .issuedAt(issueAt)
            .expiration(Date(expiration))
            .signWith(signingKey)
            .compact()
        return jwtToken
    }

    private fun isUserValid(email: String, password: String): Boolean {
        val hashedPassword = generatePasswordHash(password)
        return userRepository.existsByEmailAndPassword(email, hashedPassword)
    }

    private fun generatePasswordHash(password: String): String {
        return try {
            val messageDigest = MessageDigest.getInstance("SHA-1")
            messageDigest.update(password.toByteArray(charset("UTF-8")))
            val bytes = messageDigest.digest()
            bytes.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

  fun isTokenValid(token: String) {
        try {
            parseToken(token)
        } catch (_: io.jsonwebtoken.ExpiredJwtException){
            throw ExpiredTokenException("Expired token.")
        } catch (_: InvalidTokenException){
            throw InvalidTokenException("Invalid token.")
        } catch (_: Exception) {
            throw NotAuthorizedException("Invalid token.")
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