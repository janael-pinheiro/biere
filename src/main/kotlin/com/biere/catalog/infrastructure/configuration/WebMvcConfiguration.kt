package com.biere.catalog.infrastructure.configuration

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebMvcConfiguration(
    private val rateLimitInterceptor: RateLimitInterceptor,
    private val deprecationInterceptor: DeprecationInterceptor,
    private val contentNegotiationInterceptor: ContentNegotiationInterceptor,
    private val versioningInterceptor: VersioningInterceptor
) : WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        // Content Negotiation and Vary headers should apply to everything
        registry.addInterceptor(contentNegotiationInterceptor).addPathPatterns("/v1/**")
        
        // Rate limiting for the API
        registry.addInterceptor(rateLimitInterceptor).addPathPatterns("/v1/**")
        
        // Deprecation lifecycle headers
        registry.addInterceptor(deprecationInterceptor).addPathPatterns("/v1/**")
        
        // Versioning links
        registry.addInterceptor(versioningInterceptor).addPathPatterns("/v1/**")
    }
}
