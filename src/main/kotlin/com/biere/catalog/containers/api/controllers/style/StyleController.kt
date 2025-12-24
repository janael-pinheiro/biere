package com.biere.catalog.containers.api.controllers.style

import com.biere.catalog.containers.api.dtos.*
import com.biere.catalog.containers.api.helpers.withMethod
import com.biere.catalog.core.services.StyleService
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/v1/styles")
class StyleController(private val styleService: StyleService) {
    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun register(@RequestBody inputStyle: StyleRegistrationDTO) : ResponseEntity<ApiGeneralRegistrationResponseDTO<StyleResponseDTO>>{
        val outputStyle = ApiGeneralRegistrationResponseDTO(data=styleService.register(inputStyle))
        val selfLink = linkTo(methodOn(StyleController::class.java).getSpecificStyle(outputStyle.data.id)).withSelfRel().withMethod("GET")
        val updateLink = linkTo(methodOn(StyleController::class.java).updateStyle(outputStyle.data.id, StyleUpdateRequestDTO(""))).withRel("update_style").withMethod("PUT")
        val getAllStyles = linkTo(methodOn(StyleController::class.java).getStyles()).withRel("get_all_styles").withMethod("GET")
        outputStyle.add(selfLink, updateLink, getAllStyles)
        return ResponseEntity.created(URI("")).body(outputStyle)
    }

    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getStyles(): ResponseEntity<ApiCollectionResponseDTO<List<StyleResponseDTO>>> {
        val styles = ApiCollectionResponseDTO(data=styleService.getStyles(), page=null)
        val createStyle = linkTo(methodOn(StyleController::class.java).register(StyleRegistrationDTO(""))).withRel("create_new_style").withMethod("POST")
        styles.add(createStyle)
        return ResponseEntity.ok(styles)
    }

    @GetMapping("{styleId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getSpecificStyle(@PathVariable styleId: Long): ResponseEntity<ApiIndividualResponseDTO<StyleResponseDTO>> {
        val style = styleService.getSpecificStyle(styleId)
        val response = ApiIndividualResponseDTO(data = style)
        val selfLink = linkTo(methodOn(StyleController::class.java).getSpecificStyle(styleId)).withSelfRel().withMethod("GET")
        val updateLink = linkTo(methodOn(StyleController::class.java).updateStyle(styleId, StyleUpdateRequestDTO(""))).withRel("update_style").withMethod("PUT")
        response.add(selfLink, updateLink)
        return ResponseEntity.ok().body(response)
    }

    @PutMapping("{styleId}", consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun updateStyle(@PathVariable styleId: Long, @RequestBody style: StyleUpdateRequestDTO): ResponseEntity<ApiIndividualResponseDTO<StyleResponseDTO>> {
        val style = ApiIndividualResponseDTO(data =styleService.updateStyle(styleId, style))
        val selfLink = linkTo(methodOn(StyleController::class.java).getSpecificStyle(styleId)).withSelfRel().withMethod("GET")
        val updateLink = linkTo(methodOn(StyleController::class.java).updateStyle(styleId, StyleUpdateRequestDTO(""))).withRel("update_style").withMethod("PUT")
        style.add(selfLink, updateLink)
        return ResponseEntity.ok().body(style)
    }
}