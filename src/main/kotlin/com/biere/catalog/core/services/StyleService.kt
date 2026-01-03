package com.biere.catalog.core.services

import com.biere.catalog.adapters.entities.StyleEntity
import com.biere.catalog.adapters.output.repositories.StyleRepository
import com.biere.catalog.containers.api.dtos.StyleRegistrationDTO
import com.biere.catalog.containers.api.dtos.StyleResponseDTO
import com.biere.catalog.containers.api.dtos.StyleUpdateRequestDTO
import com.biere.catalog.core.exceptions.NotFoundException
import com.biere.catalog.core.models.StyleResponseModel
import org.springframework.stereotype.Service
import java.util.stream.Collectors

@Service
class StyleService(private val styleRepository: StyleRepository) {
    fun register(style: StyleRegistrationDTO): StyleResponseModel{
        if (styleRepository.existsByName(style.name)){
            throw NotFoundException("The name ${style.name} already exists.")
        }
        val savedStyle = styleRepository.save(StyleEntity(name = style.name))
        return StyleResponseModel(id = savedStyle.id ?: 0, name = savedStyle.name)
    }

    fun getStyles(): List<StyleResponseModel> {
        return styleRepository.findAll().stream().map { style -> StyleResponseModel(id = style.id!!, name=style.name) }.collect(
            Collectors.toList())
    }

    fun getSpecificStyle(styleId: Long): StyleResponseModel {
        val styleOptional = styleRepository.findById(styleId)
        if(styleOptional.isEmpty){
            throw NotFoundException("Style not found.")
        }
        val style = styleOptional.get()
        return StyleResponseModel(id = style.id ?: 0, name = style.name)
    }

    fun updateStyle(styleId: Long, style: StyleUpdateRequestDTO): StyleResponseModel {
        val styleEntity = styleRepository.findById(styleId).orElseThrow { NotFoundException("Style not found.") }
        styleEntity.name = style.name
        styleRepository.save(styleEntity)
        return StyleResponseModel(id = styleEntity.id!!, name = styleEntity.name)
    }

    fun deleteStyle(styleId: Long) {
        if (!styleRepository.existsById(styleId)){
            throw NotFoundException("Style not found.")
        }
        styleRepository.deleteById(styleId)
    }
}