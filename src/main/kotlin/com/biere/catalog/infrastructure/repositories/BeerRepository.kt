package com.biere.catalog.infrastructure.repositories

import com.biere.catalog.infrastructure.entities.BeerEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BeerRepository: JpaRepository<BeerEntity, Long>{
}