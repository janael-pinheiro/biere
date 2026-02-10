package com.biere.catalog.domain.port.output

import com.biere.catalog.domain.model.StyleResponseModel

interface StyleOutputPort {
    fun save(name: String): StyleResponseModel
    fun findAll(): List<StyleResponseModel>
    fun findById(id: Long): StyleResponseModel
    fun update(id: Long, name: String): StyleResponseModel
    fun delete(id: Long)
    fun existsByName(name: String): Boolean
    fun existsById(id: Long): Boolean
}
