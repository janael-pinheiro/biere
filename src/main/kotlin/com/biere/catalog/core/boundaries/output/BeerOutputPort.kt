package com.biere.catalog.core.boundaries.output

import com.biere.catalog.core.models.*

interface BeerOutputPort {
    fun getBeers(page: PageRequest): PaginatedResult<List<OutputBeerModel>>
    fun saveBeer(inputBeer: InputBeerModel): OutputBeerModel
    fun findSpecificBeer(beerId: Long): OutputBeerModel
    fun updateBeer(beerId: Long, updatedBeer: UpdateBeerModel): OutputBeerModel
    fun deleteBeer(beerId: Long)
}