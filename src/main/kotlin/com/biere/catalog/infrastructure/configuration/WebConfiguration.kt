package com.biere.catalog.infrastructure.configuration

import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.filter.ShallowEtagHeaderFilter

@Configuration
class WebConfiguration {

    @Bean
    fun shallowEtagHeaderFilter(): FilterRegistrationBean<ShallowEtagHeaderFilter> {
        val registration = FilterRegistrationBean(ShallowEtagHeaderFilter())
        registration.addUrlPatterns("/v1/*")
        registration.setName("etagFilter")
        return registration
    }
}
