package com.biere.catalog.containers.api.mappers

import com.biere.catalog.containers.api.dtos.StyleResponseDTO
import com.biere.catalog.core.models.StyleResponseModel

class StyleMapper {
    fun toStyleDTO(style: StyleResponseModel): StyleResponseDTO {
        return StyleResponseDTO(style.id, style.name)
    }
}