package com.glovo.challenge.repository

import com.glovo.challenge.data.GlovoNetworkDatasource
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class CityRepositoryTest {

    private lateinit var repository: CityRepository
    private val datasource: GlovoNetworkDatasource = mockk(relaxed = true)

    @Before
    fun setup() {
        repository = CityRepository(datasource)
    }

    @Test
    fun getCountries_useDatasource() {
        repository.getCountries()
        verify { datasource.getCountries() }
    }

    @Test
    fun getCities_useDatasource() {
        repository.getCities()
        verify { datasource.getCities() }
    }

    @Test
    fun getCity_useDatasource() {
        repository.getCity("COL")
        verify { datasource.getCity("COL") }
    }





}