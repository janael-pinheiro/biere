package com.biere.catalog.domain.port.input

import com.biere.catalog.domain.model.InputUser
import com.biere.catalog.domain.model.TokenModel

interface UserUseCase {
    fun generateToken(email: String, password: String): TokenModel
    fun refreshToken(refreshToken: String): TokenModel
    fun isTokenValid(token: String)
    fun getEmailFromToken(token: String): String
    fun getScopesFromToken(token: String): List<String>
    fun registerUser(user: InputUser): Long
    fun removeUser(userId: Long)
}
