package com.biere.catalog.domain.port.input

import com.biere.catalog.domain.model.*

interface BeerUseCase {
    fun register(inputBeer: InputBeerModel): OutputBeerModel
    fun getBeers(page: PageRequest): PaginatedResult<List<OutputBeerModel>>
    fun getSpecificBeer(beerId: Long): OutputBeerModel
    fun updateBeer(beerId: Long, beerUpdate: UpdateBeerModel): OutputBeerModel
    fun deleteBeer(beerId: Long)
}
