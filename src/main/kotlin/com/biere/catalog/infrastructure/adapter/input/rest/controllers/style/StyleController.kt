package com.biere.catalog.infrastructure.adapter.input.rest.controllers.style

import com.biere.catalog.infrastructure.adapter.input.rest.dtos.*
import com.biere.catalog.infrastructure.adapter.input.rest.presenters.StylePresenterAdapter
import com.biere.catalog.domain.exception.NotFoundException
import com.biere.catalog.domain.port.input.StyleUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.Parameter
import jakarta.validation.Valid
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

import com.biere.catalog.domain.port.output.StylePresenterPort
import com.biere.catalog.infrastructure.adapter.input.rest.controllers.Scopes
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.security.access.prepost.PreAuthorize

@RestController
@RequestMapping("/v1/styles")
@Tag(name = "Styles", description = "Style management APIs")
class StyleController(private val styleService: StyleUseCase, private val stylePresenter: StylePresenterPort) {
    @Operation(
        summary = "Register a new style",
        description = "Creates a new beer style. Verify if the style already exists via 'GET /v1/styles' before creating a new one to avoid duplicates.",
        security = [SecurityRequirement(name = "Bearer Authentication"), SecurityRequirement(name = "OAuth2 Scopes", scopes = ["SCOPE_styles:write"])]
    )
    @ApiResponses(value = [
        ApiResponse(responseCode = "201", description = "Style created successfully"),
        ApiResponse(responseCode = "400", description = "Invalid input or style already exists. Check the 'remediation' field in the response."),
        ApiResponse(responseCode = "403", description = "Insufficient scope. Requires: styles:write")
    ])
    @PreAuthorize("hasAuthority('${Scopes.STYLE_WRITE}')")
    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun register(@Valid @RequestBody inputStyle: StyleRegistrationDTO) : ResponseEntity<ApiIndividualResponseDTO<StyleResponseDTO>>{
        val outputStyle = styleService.register(inputStyle.name)
        val location = URI("/v1/styles/${outputStyle.id}")
        return ResponseEntity.created(location)
            .header("Content-Location", location.toString())
            .body(stylePresenter.prepareCreateStyle(outputStyle))
    }

    @Operation(
        summary = "Get all styles",
        description = "Retrieves all available beer styles. This is a discovery endpoint for obtaining valid 'style_id' values required for beer registration.",
        security = [SecurityRequirement(name = "Bearer Authentication"), SecurityRequirement(name = "OAuth2 Scopes", scopes = ["SCOPE_styles:read"])]
    )
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Successfully retrieved list"),
        ApiResponse(responseCode = "403", description = "Insufficient scope. Requires: styles:read")
    ])
    @PreAuthorize("hasAuthority('${Scopes.STYLE_READ}')")
    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getStyles(): ResponseEntity<ApiCollectionResponseDTO<List<ApiIndividualResponseDTO<StyleResponseDTO>>>> {
        val styles = styleService.getStyles()
        return ResponseEntity.ok(stylePresenter.prepareGetStyles(styles))
    }

    @Operation(
        summary = "Get a specific style",
        description = "Retrieves details of a specific style by ID.",
        security = [SecurityRequirement(name = "Bearer Authentication"), SecurityRequirement(name = "OAuth2 Scopes", scopes = ["SCOPE_styles:read"])]
    )
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Successfully retrieved style"),
        ApiResponse(responseCode = "403", description = "Insufficient scope. Requires: styles:read"),
        ApiResponse(responseCode = "404", description = "Style not found")
    ])
    @PreAuthorize("hasAuthority('${Scopes.STYLE_READ}')")
    @GetMapping("{styleId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getSpecificStyle(
        @Parameter(description = "ID of the style to be retrieved", example = "1")
        @PathVariable styleId: Long
    ): ResponseEntity<ApiIndividualResponseDTO<StyleResponseDTO>> {
        val style = styleService.getSpecificStyle(styleId)
        return ResponseEntity.ok().body(stylePresenter.prepareGetStyle(style))
    }

    @Operation(
        summary = "Update a style",
        description = "Updates an existing style by ID.",
        security = [SecurityRequirement(name = "Bearer Authentication"), SecurityRequirement(name = "OAuth2 Scopes", scopes = ["SCOPE_styles:write"])]
    )
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Style updated successfully"),
        ApiResponse(responseCode = "403", description = "Insufficient scope. Requires: styles:write"),
        ApiResponse(responseCode = "404", description = "Style not found")
    ])
    @PreAuthorize("hasAuthority('${Scopes.STYLE_WRITE}')")
    @PutMapping("{styleId}", consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun updateStyle(
        @Parameter(description = "ID of the style to be updated", example = "1")
        @PathVariable styleId: Long,
        @Valid @RequestBody style: StyleUpdateRequestDTO?
    ): ResponseEntity<ApiIndividualResponseDTO<StyleResponseDTO>> {
        if (style == null) {
            return ResponseEntity.notFound().build()
        }
        val updatedStyle = styleService.updateStyle(styleId, style.name)
        return ResponseEntity.ok().body(stylePresenter.prepareUpdateStyle(updatedStyle))
    }

    @Operation(
        summary = "Delete a style",
        description = "Permanently removes a style. Ensure no beers are using this style before deleting.",
        security = [SecurityRequirement(name = "Bearer Authentication"), SecurityRequirement(name = "OAuth2 Scopes", scopes = ["SCOPE_styles:write"])]
    )
    @ApiResponses(value = [
        ApiResponse(responseCode = "204", description = "Successfully deleted the style"),
        ApiResponse(responseCode = "403", description = "Insufficient scope. Requires: styles:write"),
        ApiResponse(responseCode = "404", description = "Style not found")
    ])
    @PreAuthorize("hasAuthority('${Scopes.STYLE_WRITE}')")
    @DeleteMapping("{styleId}")
    fun deleteStyle(
        @Parameter(description = "ID of the style to be deleted", example = "1")
        @PathVariable styleId: Long
    ): ResponseEntity<Void> {
        try {
            styleService.deleteStyle(styleId)
        } catch (e: NotFoundException) {
            return ResponseEntity.notFound().build()
        }
        return ResponseEntity.noContent().build()
    }
}