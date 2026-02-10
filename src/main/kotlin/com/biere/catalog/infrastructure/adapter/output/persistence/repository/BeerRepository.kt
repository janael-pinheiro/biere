package com.biere.catalog.infrastructure.adapter.output.persistence.repository

import com.biere.catalog.infrastructure.adapter.output.persistence.entity.BeerEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BeerRepository: JpaRepository<BeerEntity, Long>{
    fun existsByName(name: String): Boolean
}