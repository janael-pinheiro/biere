package com.biere.catalog.domain.exception

enum class RemediationMessage(val message: String) {
    BEER_NOT_FOUND_REMEDIATION("You need to check if a beer with that id has already been registered or correct the beer's id."),
    BEER_NAME_CONFLICT_REMEDIATION("You need to check if a beer with that name has already been registered or correct the beer's name."),
    BREWERY_NOT_FOUND_REMEDIATION("You need to check if a brewery with that id has already been registered or correct the brewery's id."),
    BREWERY_NAME_CONFLICT_REMEDIATION("You need to check if a brewery with that name has already been registered or correct the brewery's name."),
    STYLE_NOT_FOUND_REMEDIATION("You need to check if a style with that id has already been registered or correct the style's id."),
    STYLE_NAME_CONFLICT_REMEDIATION("You need to check if a style with that name has already been registered or correct the style's name."),
    TOKEN_REMEDIATION("\"The provided token has expired or is invalid. Use the 'authenticate' link to log in again or 'refresh-token' if you have a refresh token."),
    EMAIL_PASSWORD_REMEDIATION("You need to correct your email and/or password before requesting the token again."),
    COUNTRY_NOT_FOUND_REMEDIATION("You need to check if a country with that id has already been registered or correct the country's id."),
    COUNTRY_NAME_CONFLICT_REMEDIATION("You need to check if a country with that name has already been registered or correct the country's name.")
}