package com.biere.catalog.core.boundaries.output

import com.biere.catalog.core.models.InputBeerModel
import com.biere.catalog.core.models.OutputBeerModel
import com.biere.catalog.core.models.PageRequest
import com.biere.catalog.core.models.PaginatedResult
import com.biere.catalog.core.models.UpdateBeerModel

interface BeerOutputPort {
    fun getBeers(page: PageRequest): PaginatedResult<List<OutputBeerModel>>
    fun saveBeer(inputBeer: InputBeerModel): OutputBeerModel
    fun findSpecificBeer(beerId: Long): OutputBeerModel
    fun updateBeer(beerId: Long, updatedBeer: UpdateBeerModel): OutputBeerModel
    fun deleteBeer(beerId: Long)
}