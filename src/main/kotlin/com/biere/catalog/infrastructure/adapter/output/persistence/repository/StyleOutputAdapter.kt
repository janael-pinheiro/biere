package com.biere.catalog.infrastructure.adapter.output.persistence.repository

import com.biere.catalog.domain.model.StyleResponseModel
import com.biere.catalog.domain.port.output.StyleOutputPort
import com.biere.catalog.infrastructure.adapter.output.persistence.entity.StyleEntity
import com.biere.catalog.domain.exception.NotFoundException
import org.springframework.stereotype.Component

@Component
class StyleOutputAdapter(
    private val styleRepository: StyleRepository
) : StyleOutputPort {

    override fun save(name: String): StyleResponseModel {
        val style = styleRepository.save(StyleEntity(name = name))
        return mapToModel(style)
    }

    override fun findAll(): List<StyleResponseModel> {
        return styleRepository.findAll().map { mapToModel(it) }
    }

    override fun findById(id: Long): StyleResponseModel {
        val style = styleRepository.findById(id).orElseThrow { NotFoundException("Style $id not found.") }
        return mapToModel(style)
    }

    override fun update(id: Long, name: String): StyleResponseModel {
        val style = styleRepository.findById(id).orElseThrow { NotFoundException("Style $id not found.") }
        style.name = name
        return mapToModel(styleRepository.save(style))
    }

    override fun delete(id: Long) {
        styleRepository.deleteById(id)
    }

    override fun existsByName(name: String): Boolean {
        return styleRepository.existsByName(name)
    }

    override fun existsById(id: Long): Boolean {
        return styleRepository.existsById(id)
    }

    private fun mapToModel(entity: StyleEntity): StyleResponseModel {
        return StyleResponseModel(
            id = entity.id ?: 0,
            name = entity.name
        )
    }
}
