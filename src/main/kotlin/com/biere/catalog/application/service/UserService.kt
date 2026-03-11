package com.biere.catalog.application.service

import com.biere.catalog.domain.port.output.UserOutputPort
import com.biere.catalog.domain.port.input.UserUseCase
import com.biere.catalog.domain.model.TokenModel
import com.biere.catalog.domain.model.InputUser
import com.biere.catalog.domain.exception.NotAuthorizedException
import com.biere.catalog.domain.exception.InvalidTokenException
import com.biere.catalog.domain.exception.ExpiredTokenException
import com.biere.catalog.domain.exception.RemediationMessage
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.security.Key
import java.security.MessageDigest
import java.util.Date

@Service
class UserService(
    @Value("\${jwt.secret}") private val secret: String,
    @Value("\${access-token.expiration}") private val accessTokenExpiration: Long,
    @Value("\${refresh-token.expiration}") private val refreshTokenExpiration: Long,
    private val userOutputPort: UserOutputPort,
    private final val logger: Logger = LoggerFactory.getLogger(UserService::class.java)
) : UserUseCase {
    private val signingKey: Key by lazy {
        Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret))
    }
    override fun generateToken(email: String, password: String): TokenModel {
        if(!isUserValid(email, password)){
            throw NotAuthorizedException(message = "E-mail or password incorrect.", remediation = RemediationMessage.EMAIL_PASSWORD_REMEDIATION.message)
        }
        val now = Date()
        val accessToken = createToken(email, now.time + accessTokenExpiration, now)
        val refreshToken = createToken(email, now.time + refreshTokenExpiration, now)
        logger.info("Generating token for ${email}")
        return TokenModel(accessToken = accessToken, refreshToken = refreshToken)
    }

    override fun refreshToken(refreshToken: String): TokenModel {
        val now = Date()
        val accessToken = createToken("", now.time + accessTokenExpiration, now)
        val newRefreshToken = createToken("", now.time + refreshTokenExpiration, now)
        return TokenModel(accessToken = accessToken, refreshToken = newRefreshToken)
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
        return userOutputPort.existsByEmailAndPassword(email, hashedPassword)
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

    override fun isTokenValid(token: String) {
        try {
            parseToken(token)
        } catch (_: io.jsonwebtoken.ExpiredJwtException){
            throw ExpiredTokenException(message = "Expired token.", remediation = RemediationMessage.TOKEN_REMEDIATION.message)
        } catch (_: InvalidTokenException){
            throw InvalidTokenException(message = "Invalid token.", remediation = RemediationMessage.TOKEN_REMEDIATION.message)
        } catch (_: Exception) {
            throw NotAuthorizedException(message = "Invalid token.", remediation = RemediationMessage.TOKEN_REMEDIATION.message)
        }
    }

    override fun getEmailFromToken(token: String): String {
        val parsedToken = parseToken(token)
        return parsedToken.payload.subject
    }

    private fun parseToken(token: String): Jws<Claims> {
        return Jwts.parser()
            .verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret)))
            .build()
            .parseSignedClaims(token)
    }

    override fun registerUser(user: InputUser): Long {
        val encodedPassword = generatePasswordHash(user.password)
        return userOutputPort.save(user, encodedPassword)
    }

    override fun removeUser(userId: Long) {
        userOutputPort.deleteById(userId)
    }
}