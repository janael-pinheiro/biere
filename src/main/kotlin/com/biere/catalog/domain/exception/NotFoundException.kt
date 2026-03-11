package com.biere.catalog.domain.exception

class NotFoundException(message: String, val remediation: String): Exception(message) {
}