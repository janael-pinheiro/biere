package com.biere.catalog.adapters.repositories

import com.biere.catalog.adapters.entities.StyleEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StyleRepository: JpaRepository<StyleEntity, Long> {
}