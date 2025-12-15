package com.biere.catalog.adapters.output.repositories

import com.biere.catalog.adapters.entities.CountryEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CountryRepository: JpaRepository<CountryEntity, Long> {
    fun existsByName(name: String): Boolean
}