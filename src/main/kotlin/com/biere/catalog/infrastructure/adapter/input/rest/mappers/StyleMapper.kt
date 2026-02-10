package com.biere.catalog.infrastructure.adapter.input.rest.mappers

import com.biere.catalog.infrastructure.adapter.input.rest.dtos.StyleResponseDTO
import com.biere.catalog.domain.model.StyleResponseModel

class StyleMapper {
    fun toStyleDTO(style: StyleResponseModel): StyleResponseDTO {
        return StyleResponseDTO(style.id, style.name)
    }
}