package com.biere.catalog.containers.api.presenters

import com.biere.catalog.containers.api.controllers.brewery.BreweryController
import com.biere.catalog.containers.api.controllers.style.StyleController
import com.biere.catalog.containers.api.dtos.ApiCollectionResponseDTO
import com.biere.catalog.containers.api.dtos.ApiIndividualResponseDTO
import com.biere.catalog.containers.api.dtos.BreweryResponseDTO
import com.biere.catalog.containers.api.dtos.StyleRegistrationDTO
import com.biere.catalog.containers.api.dtos.StyleResponseDTO
import com.biere.catalog.containers.api.dtos.StyleUpdateRequestDTO
import com.biere.catalog.containers.api.helpers.withMethod
import com.biere.catalog.containers.api.mappers.StyleMapper
import com.biere.catalog.core.models.StyleResponseModel
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.afford
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import org.springframework.hateoas.server.mvc.afford

class StylePresenterAdapter(private val styleMapper: StyleMapper = StyleMapper()) {
    fun prepareGetStyle(styleModel: StyleResponseModel): ApiIndividualResponseDTO<StyleResponseDTO> {
        val styleDto = styleMapper.toStyleDTO(styleModel)
        val styleResponse = ApiIndividualResponseDTO(data = styleDto)
        this.addSelfLink(styleResponse)
        this.addUpdateSelfLink(styleResponse)
        this.addDeleteLink(styleResponse)
        return styleResponse
    }

    fun prepareUpdateStyle(styleModel: StyleResponseModel): ApiIndividualResponseDTO<StyleResponseDTO> {
        return this.prepareGetStyle(styleModel)
    }

    fun prepareGetStyles(styles: List<StyleResponseModel>): ApiCollectionResponseDTO<List<ApiIndividualResponseDTO<StyleResponseDTO>>> {
        val response = styles.map { style -> prepareGetStyle(style) }
        val stylesResponse = ApiCollectionResponseDTO(data = response, page = null)
        this.addCreateStyleLink(stylesResponse)
        return stylesResponse
    }

    fun prepareCreateStyle(style: StyleResponseModel): ApiIndividualResponseDTO<StyleResponseDTO> {
        val styleResponse = ApiIndividualResponseDTO(data = styleMapper.toStyleDTO(style))
        this.addSelfLink(styleResponse)
        this.addUpdateSelfLink(styleResponse)
        this.addGetAllStylesLink(styleResponse)
        return styleResponse
    }
    private fun addSelfLink(response: ApiIndividualResponseDTO<StyleResponseDTO>) {
        response.add(linkTo(methodOn(StyleController::class.java)
            .getSpecificStyle(response.data.id))
            .withSelfRel()
            .withMethod("GET"))
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