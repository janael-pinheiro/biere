package com.biere.catalog.configuration

import com.biere.catalog.adapters.output.repositories.BeerOutputAdapter
import com.biere.catalog.adapters.output.repositories.BeerRepository
import com.biere.catalog.adapters.output.repositories.BreweryRepository
import com.biere.catalog.adapters.output.repositories.StyleRepository
import com.biere.catalog.containers.api.presenters.BeerPresenterOutputAdapter
import com.biere.catalog.core.boundaries.output.BeerOutputPort
import com.biere.catalog.core.boundaries.output.BeerPresenterOutputPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class BeanFactory {
    @Bean
    fun createBeerOutputPort(beerRepository: BeerRepository, breweryRepository: BreweryRepository, styleRepository: StyleRepository): BeerOutputPort {
        return BeerOutputAdapter(beerRepository, breweryRepository, styleRepository)
    }

    @Bean
    fun createBeerPresenterOutputPort(): BeerPresenterOutputPort {
        return BeerPresenterOutputAdapter()
    }
}