package com.biere.catalog.core.services

import com.biere.catalog.adapters.entities.StyleEntity
import com.biere.catalog.adapters.output.repositories.StyleRepository
import com.biere.catalog.containers.api.dtos.StyleRegistrationDTO
import com.biere.catalog.containers.api.dtos.StyleResponseDTO
import com.biere.catalog.core.exceptions.NotFoundException
import org.springframework.stereotype.Service
import java.util.stream.Collectors

@Service
class StyleService(private val styleRepository: StyleRepository) {
    fun register(style: StyleRegistrationDTO): StyleResponseDTO{
        val savedStyle = styleRepository.save(StyleEntity(name = style.name))
        return StyleResponseDTO(id = savedStyle.id ?: 0, name = savedStyle.name)
    }

    fun getStyles(): List<StyleResponseDTO> {
        return styleRepository.findAll().stream().map { style -> StyleResponseDTO(id = style.id!!, name=style.name) }.collect(
            Collectors.toList())
    }

    fun getSpecificStyle(styleId: Long): StyleResponseDTO {
        val styleOptional = styleRepository.findById(styleId)
        if(styleOptional.isEmpty){
            throw NotFoundException("Style not found.")
        }
        val style = styleOptional.get()
        return StyleResponseDTO(id = style.id ?: 0, name = style.name)
    }
}