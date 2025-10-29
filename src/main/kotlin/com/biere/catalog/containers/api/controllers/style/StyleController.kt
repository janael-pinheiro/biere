package com.biere.catalog.containers.api.controllers.style

import com.biere.catalog.containers.api.dtos.ApiGeneralRegistrationResponseDTO
import com.biere.catalog.containers.api.dtos.StyleRegistrationDTO
import com.biere.catalog.containers.api.dtos.StyleResponseDTO
import com.biere.catalog.core.services.StyleService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping("/v1/styles")
class StyleController(private val styleService: StyleService) {
    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun register(@RequestBody inputStyle: StyleRegistrationDTO) : ResponseEntity<ApiGeneralRegistrationResponseDTO<StyleResponseDTO>>{
        val outputStyle = styleService.register(inputStyle)
        val apiResponse = ApiGeneralRegistrationResponseDTO(data = outputStyle, links = listOf(
            "GET /v1/styles/${outputStyle.id}"))
        return ResponseEntity.created(URI("")).body(apiResponse)
    }

    @GetMapping("{styleId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getSpecificStyle(@PathVariable styleId: Long): ResponseEntity<StyleResponseDTO> {
        val style = styleService.getSpecificStyle(styleId)
        return ResponseEntity.ok().body(style)
    }
}