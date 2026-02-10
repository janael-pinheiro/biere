package com.biere.catalog.domain.port.input

import com.biere.catalog.domain.model.StyleResponseModel

interface StyleUseCase {
    fun register(name: String): StyleResponseModel
    fun getStyles(): List<StyleResponseModel>
    fun getSpecificStyle(styleId: Long): StyleResponseModel
    fun updateStyle(styleId: Long, name: String): StyleResponseModel
    fun deleteStyle(styleId: Long)
}
