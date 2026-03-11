package com.biere.catalog.domain.exception

class NotAuthorizedException(message: String, val remediation: String) : Exception(message) {
}