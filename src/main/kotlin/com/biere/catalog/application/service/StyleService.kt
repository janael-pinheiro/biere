package com.biere.catalog.application.service

import com.biere.catalog.domain.port.output.StyleOutputPort
import com.biere.catalog.domain.port.input.StyleUseCase
import com.biere.catalog.domain.model.StyleResponseModel
import com.biere.catalog.domain.exception.ConflictException
import com.biere.catalog.domain.exception.NotFoundException
import com.biere.catalog.domain.exception.RemediationMessage
import org.springframework.stereotype.Service

@Service
class StyleService(private val styleOutputPort: StyleOutputPort) : StyleUseCase {
    override fun register(name: String): StyleResponseModel{
        if (styleOutputPort.existsByName(name)){
            throw ConflictException(message = "The name ${name} already exists.",
                remediation = RemediationMessage.STYLE_NAME_CONFLICT_REMEDIATION.message
            )
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
            throw NotFoundException(message = "Style not found.", remediation = RemediationMessage.STYLE_NOT_FOUND_REMEDIATION.message)
        }
        return styleOutputPort.update(styleId, name)
    }

    override fun deleteStyle(styleId: Long) {
        if (!styleOutputPort.existsById(styleId)){
            throw NotFoundException(message = "Style not found.", remediation = RemediationMessage.STYLE_NOT_FOUND_REMEDIATION.message)
        }
        styleOutputPort.delete(styleId)
    }
}