package com.biere.catalog.application.service

import com.biere.catalog.domain.port.output.StyleOutputPort
import com.biere.catalog.domain.port.input.StyleUseCase
import com.biere.catalog.domain.model.StyleResponseModel
import com.biere.catalog.domain.exception.ConflictException
import com.biere.catalog.domain.exception.NotFoundException
import org.springframework.stereotype.Service

@Service
class StyleService(private val styleOutputPort: StyleOutputPort) : StyleUseCase {
    override fun register(name: String): StyleResponseModel{
        if (styleOutputPort.existsByName(name)){
            throw ConflictException("The name ${name} already exists.")
        }
        return styleOutputPort.save(name)
    }

    override fun getStyles(): List<StyleResponseModel> {
        return styleOutputPort.findAll()
    }

    override fun getSpecificStyle(styleId: Long): StyleResponseModel {
        return styleOutputPort.findById(styleId)
    }

    override fun updateStyle(styleId: Long, name: String): StyleResponseModel {
        if (!styleOutputPort.existsById(styleId)) {
            throw NotFoundException("Style not found.")
        }
        return styleOutputPort.update(styleId, name)
    }

    override fun deleteStyle(styleId: Long) {
        if (!styleOutputPort.existsById(styleId)){
            throw NotFoundException("Style not found.")
        }
        styleOutputPort.delete(styleId)
    }
}