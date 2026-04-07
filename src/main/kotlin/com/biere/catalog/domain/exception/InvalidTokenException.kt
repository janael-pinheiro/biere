package com.biere.catalog.domain.exception

class InvalidTokenException(message: String, val remediation: String): Exception(message) {
}