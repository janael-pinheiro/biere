package com.biere.catalog.domain.port.output

import com.biere.catalog.domain.model.CountryModel

interface CountryOutputPort {
    fun save(name: String): CountryModel
    fun findAll(): List<CountryModel>
    fun findById(id: Long): CountryModel
    fun update(id: Long, name: String): CountryModel
    fun delete(id: Long)
    fun existsByName(name: String): Boolean
    fun existsById(id: Long): Boolean
}
