package com.biere.catalog.infrastructure.repositories

import com.biere.catalog.infrastructure.entities.CountryEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CountryRepository: JpaRepository<CountryEntity, Long> {
}