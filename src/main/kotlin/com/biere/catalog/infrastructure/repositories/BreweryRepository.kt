package com.biere.catalog.infrastructure.repositories

import com.biere.catalog.infrastructure.entities.BreweryEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BreweryRepository: JpaRepository<BreweryEntity, Long> {
}