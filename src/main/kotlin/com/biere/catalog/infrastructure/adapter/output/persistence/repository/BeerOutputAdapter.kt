package com.biere.catalog.infrastructure.adapter.output.persistence.repository

import com.biere.catalog.infrastructure.adapter.output.persistence.entity.BeerEntityMapper
import com.biere.catalog.domain.port.output.BeerOutputPort
import com.biere.catalog.domain.exception.ConflictException
import com.biere.catalog.domain.exception.NotFoundException
import com.biere.catalog.domain.exception.RemediationMessage
import com.biere.catalog.domain.model.*
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component
import java.util.*

@Component
class BeerOutputAdapter(
    private val beerRepository: BeerRepository,
    private val breweryRepository: BreweryRepository,
    private val styleRepository: StyleRepository): BeerOutputPort {

    override fun getBeers(page: PageRequest): PaginatedResult<List<OutputBeerModel>> {
        val size = page.size.coerceAtMost(100)
        val beers = beerRepository.findAll(org.springframework.data.domain.PageRequest.of(page.number, size, Sort.by(page.sort)))
        val outputBeers: List<OutputBeerModel> = beers.stream().map( BeerEntityMapper::mapToOutputBeer).toList()
        val lastPage = beers.totalPages - 1
        val nextPage = if (page.number < lastPage) page.number + 1 else page.number
        val previousPage = if (page.number > 0) page.number - 1 else page.number
        val outputPage = PageMetadata(totalElements = beers.totalElements, totalPages = beers.totalPages, 0, lastPage, nextPage ,previousPage, page.number)
        return PaginatedResult(outputBeers, outputPage)
    }

    override fun saveBeer(inputBeer: InputBeerModel): OutputBeerModel {
        if (beerRepository.existsByName(inputBeer.name)) {
            throw ConflictException(message = "Beer with name ${inputBeer.name} already exists.", remediation = RemediationMessage.BEER_NAME_CONFLICT_REMEDIATION.message)
        }
        val brewery = breweryRepository.findById(inputBeer.breweryId).orElseThrow { NotFoundException(message = "Brewery with id ${inputBeer.breweryId} does not exist.", remediation = RemediationMessage.BREWERY_NOT_FOUND_REMEDIATION.message) }
        val style = styleRepository.findById(inputBeer.styleId).orElseThrow { NotFoundException(message = "Style with id ${inputBeer.styleId} does not exist.", remediation = RemediationMessage.STYLE_NOT_FOUND_REMEDIATION.message) }
        val beer = BeerEntityMapper.mapToEntity(inputBeer, brewery, style)
        return BeerEntityMapper.mapToOutputBeer(beerRepository.save(beer))
    }

    override fun findSpecificBeer(beerId: Long): OutputBeerModel {
        val optionalBeer = beerRepository.findById(beerId)
        if(optionalBeer.isEmpty){
            throw NotFoundException(message = "Beer not found.", remediation = RemediationMessage.BEER_NOT_FOUND_REMEDIATION.message)
        }
        return BeerEntityMapper.mapToOutputBeer(optionalBeer.get())
    }

    override fun updateBeer(beerId: Long, updatedBeer: UpdateBeerModel): OutputBeerModel {
        val optionalBeer = beerRepository.findById(beerId)
        if(optionalBeer.isEmpty){
            throw NotFoundException(message = "Beer not found.", remediation = RemediationMessage.BEER_NOT_FOUND_REMEDIATION.message)
        }

        val beer = optionalBeer.get()
        beer.name = updatedBeer.name ?: beer.name
        beer.alcoholContent = updatedBeer.alcoholContent ?: beer.alcoholContent
        beer.year = updatedBeer.year ?: beer.year

        if (Objects.nonNull(updatedBeer.breweryId)) {
            val brewery = breweryRepository.findById(updatedBeer.breweryId!!)
                .orElseThrow{ NotFoundException(message = "Brewery not found.", remediation = RemediationMessage.BREWERY_NOT_FOUND_REMEDIATION.message) }
            beer.brewery = brewery ?: beer.brewery
        }

        return BeerEntityMapper.mapToOutputBeer(beerRepository.save(beer))
    }

    override fun deleteBeer(beerId: Long) {
        if (!this.beerRepository.existsById(beerId)){
            throw NotFoundException(message = "Beer not found.", remediation = RemediationMessage.BEER_NOT_FOUND_REMEDIATION.message)
        }
        beerRepository.deleteById(beerId)
    }
}