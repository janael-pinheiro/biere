package com.biere.catalog.adapters.output.repositories

import com.biere.catalog.adapters.entities.BeerEntityMapper
import com.biere.catalog.core.boundaries.output.BeerOutputPort
import com.biere.catalog.core.exceptions.NotFoundException
import com.biere.catalog.core.models.*
import org.springframework.data.domain.Sort
import java.util.Objects

class BeerOutputAdapter(
    private val beerRepository: BeerRepository,
    private val breweryRepository: BreweryRepository,
    private val styleRepository: StyleRepository): BeerOutputPort {

    override fun getBeers(page: PageRequest): PaginatedResult<List<OutputBeerModel>> {
        val size = page.size.coerceAtMost(100)
        val beers = beerRepository.findAll(org.springframework.data.domain.PageRequest.of(page.number, size, Sort.by(page.sort)))
        val outputBeers: List<OutputBeerModel> = beers.stream().map( BeerEntityMapper::mapToOutputBeer).toList()
        val outputPage = PageMetadata(totalElements = beers.totalElements, totalPages = beers.totalPages, null, null, null ,null, page.number)
        return PaginatedResult(outputBeers, outputPage)
    }

    override fun saveBeer(inputBeer: InputBeerModel): OutputBeerModel {
        val brewery = breweryRepository.findById(inputBeer.breweryId).get()
        val style = styleRepository.findById(inputBeer.styleId).get()
        val beer = BeerEntityMapper.mapToEntity(inputBeer, brewery, style)
        return BeerEntityMapper.mapToOutputBeer(beerRepository.save(beer))
    }

    override fun findSpecificBeer(beerId: Long): OutputBeerModel {
        val optionalBeer = beerRepository.findById(beerId)
        if(optionalBeer.isEmpty){
            throw NotFoundException("Beer not found.")
        }
        return BeerEntityMapper.mapToOutputBeer(optionalBeer.get())
    }

    override fun updateBeer(beerId: Long, updatedBeer: UpdateBeerModel): OutputBeerModel {
        val optionalBeer = beerRepository.findById(beerId)
        if(optionalBeer.isEmpty){
            throw NotFoundException("Beer not found.")
        }

        val beer = optionalBeer.get()
        beer.name = updatedBeer.name ?: beer.name
        beer.alcoholContent = updatedBeer.alcoholContent ?: beer.alcoholContent
        beer.year = updatedBeer.year ?: beer.year

        if (Objects.nonNull(updatedBeer.breweryId)) {
            val brewery = breweryRepository.findById(updatedBeer.breweryId!!)
                .orElseThrow{ NotFoundException("Brewery not found.") }
            beer.brewery = brewery ?: beer.brewery
        }

        return BeerEntityMapper.mapToOutputBeer(beerRepository.save(beer))
    }

    override fun deleteBeer(beerId: Long) {
        beerRepository.deleteById(beerId)
    }
}