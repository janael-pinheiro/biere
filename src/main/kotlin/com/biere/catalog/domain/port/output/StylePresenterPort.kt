package com.biere.catalog.domain.port.output

import com.biere.catalog.domain.model.StyleResponseModel
import com.biere.catalog.infrastructure.adapter.input.rest.dtos.ApiCollectionResponseDTO
import com.biere.catalog.infrastructure.adapter.input.rest.dtos.ApiIndividualResponseDTO
import com.biere.catalog.infrastructure.adapter.input.rest.dtos.StyleResponseDTO

interface StylePresenterPort {
    fun prepareGetStyle(styleModel: StyleResponseModel): ApiIndividualResponseDTO<StyleResponseDTO>
    fun prepareUpdateStyle(styleModel: StyleResponseModel): ApiIndividualResponseDTO<StyleResponseDTO>
    fun prepareGetStyles(styles: List<StyleResponseModel>): ApiCollectionResponseDTO<List<ApiIndividualResponseDTO<StyleResponseDTO>>>
    fun prepareCreateStyle(style: StyleResponseModel): ApiIndividualResponseDTO<StyleResponseDTO>
}
