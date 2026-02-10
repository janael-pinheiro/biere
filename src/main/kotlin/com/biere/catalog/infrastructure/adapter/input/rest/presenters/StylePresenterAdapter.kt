package com.biere.catalog.infrastructure.adapter.input.rest.presenters

import com.biere.catalog.infrastructure.adapter.input.rest.controllers.brewery.BreweryController
import com.biere.catalog.infrastructure.adapter.input.rest.controllers.style.StyleController
import com.biere.catalog.infrastructure.adapter.input.rest.dtos.ApiCollectionResponseDTO
import com.biere.catalog.infrastructure.adapter.input.rest.dtos.ApiIndividualResponseDTO
import com.biere.catalog.infrastructure.adapter.input.rest.dtos.BreweryResponseDTO
import com.biere.catalog.infrastructure.adapter.input.rest.dtos.StyleRegistrationDTO
import com.biere.catalog.infrastructure.adapter.input.rest.dtos.StyleResponseDTO
import com.biere.catalog.infrastructure.adapter.input.rest.dtos.StyleUpdateRequestDTO
import com.biere.catalog.infrastructure.adapter.input.rest.helpers.withMethod
import com.biere.catalog.infrastructure.adapter.input.rest.mappers.StyleMapper
import com.biere.catalog.domain.model.StyleResponseModel
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.afford
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import com.biere.catalog.domain.port.output.StylePresenterPort
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import org.springframework.stereotype.Component

@Component
class StylePresenterAdapter(private val styleMapper: StyleMapper = StyleMapper()) : StylePresenterPort {
    override fun prepareGetStyle(styleModel: StyleResponseModel): ApiIndividualResponseDTO<StyleResponseDTO> {
        val styleDto = styleMapper.toStyleDTO(styleModel)
        val styleResponse = ApiIndividualResponseDTO(data = styleDto)
        this.addSelfLink(styleResponse)
        this.addUpdateSelfLink(styleResponse)
        this.addDeleteLink(styleResponse)
        return styleResponse
    }

    override fun prepareUpdateStyle(styleModel: StyleResponseModel): ApiIndividualResponseDTO<StyleResponseDTO> {
        return this.prepareGetStyle(styleModel)
    }

    override fun prepareGetStyles(styles: List<StyleResponseModel>): ApiCollectionResponseDTO<List<ApiIndividualResponseDTO<StyleResponseDTO>>> {
        val response = styles.map { style -> prepareGetStyle(style) }
        val stylesResponse = ApiCollectionResponseDTO(data = response, page = null)
        this.addCreateStyleLink(stylesResponse)
        return stylesResponse
    }

    override fun prepareCreateStyle(style: StyleResponseModel): ApiIndividualResponseDTO<StyleResponseDTO> {
        val styleResponse = ApiIndividualResponseDTO(data = styleMapper.toStyleDTO(style))
        this.addSelfLink(styleResponse)
        this.addUpdateSelfLink(styleResponse)
        this.addGetAllStylesLink(styleResponse)
        return styleResponse
    }
    private fun addSelfLink(response: ApiIndividualResponseDTO<StyleResponseDTO>) {
        val selfLink = linkTo(methodOn(StyleController::class.java).getSpecificStyle(response.data.id))
            .withSelfRel()
            .andAffordance(afford(methodOn(StyleController::class.java).updateStyle(response.data.id, StyleUpdateRequestDTO(""))))
            .andAffordance(afford(methodOn(StyleController::class.java).deleteStyle(response.data.id)))
            .withMethod("GET")
            
        response.add(selfLink)
    }

    private fun addUpdateSelfLink(response: ApiIndividualResponseDTO<StyleResponseDTO>) {
        response.add(linkTo(methodOn(StyleController::class.java)
            .updateStyle(response.data.id, StyleUpdateRequestDTO("")))
            .withRel("update_style")
            .withMethod("PUT"))
    }

    private fun addDeleteLink(response: ApiIndividualResponseDTO<StyleResponseDTO>) {
        response.add(linkTo(methodOn(StyleController::class.java)
            .deleteStyle(response.data.id))
            .withRel("delete_style")
            .withMethod("DELETE"))
    }

    private fun addCreateStyleLink(response: ApiCollectionResponseDTO<List<ApiIndividualResponseDTO<StyleResponseDTO>>>) {
        response.add(linkTo(methodOn(StyleController::class.java)
            .register(StyleRegistrationDTO("")))
            .withRel("create_new_style")
            .withMethod("POST"))
    }

    private fun addGetAllStylesLink(response: ApiIndividualResponseDTO<StyleResponseDTO>) {
        response.add(linkTo(methodOn(StyleController::class.java)
            .getStyles())
            .withRel("get_all_styles")
            .withMethod("GET"))
    }
}