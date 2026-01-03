package com.biere.catalog.containers.api.controllers.style

import com.biere.catalog.containers.api.dtos.*
import com.biere.catalog.containers.api.helpers.withMethod
import com.biere.catalog.containers.api.mappers.StyleMapper
import com.biere.catalog.containers.api.presenters.StylePresenterAdapter
import com.biere.catalog.core.exceptions.NotFoundException
import com.biere.catalog.core.services.StyleService
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses

@RestController
@RequestMapping("/v1/styles")
@Tag(name = "Styles", description = "Style management APIs")
class StyleController(private val styleService: StyleService, private val stylePresenter: StylePresenterAdapter = StylePresenterAdapter()) {
    @Operation(summary = "Register a new style", description = "Creates a new beer style.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "201", description = "Style created successfully"),
        ApiResponse(responseCode = "400", description = "Invalid input")
    ])
    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun register(@RequestBody inputStyle: StyleRegistrationDTO) : ResponseEntity<ApiIndividualResponseDTO<StyleResponseDTO>>{
        val outputStyle = styleService.register(inputStyle)
        return ResponseEntity.created(URI("/v1/styles/${outputStyle.id}")).body(stylePresenter.prepareCreateStyle(outputStyle))
    }

    @Operation(summary = "Get all styles", description = "Retrieves a list of all available beer styles.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Successfully retrieved list")
    ])
    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getStyles(): ResponseEntity<ApiCollectionResponseDTO<List<ApiIndividualResponseDTO<StyleResponseDTO>>>> {
        val styles = styleService.getStyles()
        return ResponseEntity.ok(stylePresenter.prepareGetStyles(styles))
    }

    @Operation(summary = "Get a specific style", description = "Retrieves details of a specific style by ID.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Successfully retrieved style"),
        ApiResponse(responseCode = "404", description = "Style not found")
    ])
    @GetMapping("{styleId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getSpecificStyle(@PathVariable styleId: Long): ResponseEntity<ApiIndividualResponseDTO<StyleResponseDTO>> {
        val style = styleService.getSpecificStyle(styleId)
        return ResponseEntity.ok().body(stylePresenter.prepareGetStyle(style))
    }

    @Operation(summary = "Update a style", description = "Updates an existing style by ID.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Style updated successfully"),
        ApiResponse(responseCode = "404", description = "Style not found")
    ])
    @PutMapping("{styleId}", consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun updateStyle(@PathVariable styleId: Long, @RequestBody style: StyleUpdateRequestDTO?): ResponseEntity<ApiIndividualResponseDTO<StyleResponseDTO>> {
        if (style == null) {
            return ResponseEntity.notFound().build()
        }
        val style = styleService.updateStyle(styleId, style)
        return ResponseEntity.ok().body(stylePresenter.prepareUpdateStyle(style))
    }

    @Operation(summary = "Delete a style", description = "Deletes an existing style by ID.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Successfully deleted the style"),
        ApiResponse(responseCode = "404", description = "Style not found")
    ])
    @DeleteMapping("{styleId}")
    fun deleteStyle(@PathVariable styleId: Long): ResponseEntity<Void> {
        try {
            styleService.deleteStyle(styleId)
        } catch (e: NotFoundException) {
            return ResponseEntity.notFound().build()
        }
        return ResponseEntity.noContent().build()
    }
}