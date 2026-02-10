package com.biere.catalog.domain.port.input

import com.biere.catalog.domain.model.CountryModel

interface CountryUseCase {
    fun register(name: String): CountryModel
    fun getCountries(): List<CountryModel>
    fun getSpecificCountry(countryId: Long): CountryModel
    fun updateCountry(countryId: Long, name: String): CountryModel
    fun deleteCountry(countryId: Long)
}
