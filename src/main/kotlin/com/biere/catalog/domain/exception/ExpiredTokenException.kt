package com.biere.catalog.domain.exception

class ExpiredTokenException(message: String, val remediation: String): Exception(message) {
}