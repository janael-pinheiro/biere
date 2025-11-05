package com.biere.catalog.adapters.output.repositories

import com.biere.catalog.adapters.entities.BreweryEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BreweryRepository: JpaRepository<BreweryEntity, Long> {
}