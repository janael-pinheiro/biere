package com.biere.catalog.domain.port.output

import com.biere.catalog.domain.model.*

interface BeerOutputPort {
    fun getBeers(page: PageRequest): PaginatedResult<List<OutputBeerModel>>
    fun saveBeer(inputBeer: InputBeerModel): OutputBeerModel
    fun findSpecificBeer(beerId: Long): OutputBeerModel
    fun updateBeer(beerId: Long, updatedBeer: UpdateBeerModel): OutputBeerModel
    fun deleteBeer(beerId: Long)
}