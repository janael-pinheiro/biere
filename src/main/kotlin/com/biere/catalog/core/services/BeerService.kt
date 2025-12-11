package com.biere.catalog.core.services

import com.biere.catalog.core.boundaries.output.BeerOutputPort
import com.biere.catalog.core.models.InputBeerModel
import com.biere.catalog.core.models.OutputBeerModel
import com.biere.catalog.core.models.PageRequest
import com.biere.catalog.core.models.PaginatedResult
import com.biere.catalog.core.models.UpdateBeerModel
import org.springframework.stereotype.Service

@Service
class BeerService(
    private val beerOutputPort: BeerOutputPort) {
    fun register(inputBeer: InputBeerModel): OutputBeerModel {
        return beerOutputPort.saveBeer(inputBeer)
    }

    fun getBeers(page: PageRequest): PaginatedResult<List<OutputBeerModel>> {
        return beerOutputPort.getBeers(page)
    }

    fun getSpecificBeer(beerId: Long): OutputBeerModel {
        return beerOutputPort.findSpecificBeer(beerId)
    }

    fun updateBeer(beerId: Long, beerUpdate: UpdateBeerModel): OutputBeerModel {
        return beerOutputPort.updateBeer(beerId, beerUpdate)
    }

    fun deleteBeer(beerId: Long) {
        beerOutputPort.deleteBeer(beerId)
    }
}