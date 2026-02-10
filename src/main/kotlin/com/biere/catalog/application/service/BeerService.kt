package com.biere.catalog.application.service

import com.biere.catalog.domain.port.output.BeerOutputPort
import com.biere.catalog.domain.model.*
import com.biere.catalog.domain.port.input.BeerUseCase
import org.springframework.stereotype.Service

@Service
class BeerService(
    private val beerOutputPort: BeerOutputPort): BeerUseCase {
    override fun register(inputBeer: InputBeerModel): OutputBeerModel {
        return beerOutputPort.saveBeer(inputBeer)
    }

    override fun getBeers(page: PageRequest): PaginatedResult<List<OutputBeerModel>> {
        return beerOutputPort.getBeers(page)
    }

    override fun getSpecificBeer(beerId: Long): OutputBeerModel {
        return beerOutputPort.findSpecificBeer(beerId)
    }

    override fun updateBeer(beerId: Long, beerUpdate: UpdateBeerModel): OutputBeerModel {
        return beerOutputPort.updateBeer(beerId, beerUpdate)
    }

    override fun deleteBeer(beerId: Long) {
        beerOutputPort.deleteBeer(beerId)
    }
}