package com.biere.catalog.adapters.output.repositories

import com.biere.catalog.adapters.entities.BeerEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BeerRepository: JpaRepository<BeerEntity, Long>{
}