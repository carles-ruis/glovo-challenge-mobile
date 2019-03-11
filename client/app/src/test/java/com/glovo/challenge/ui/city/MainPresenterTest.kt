package com.glovo.challenge.ui.city

import android.location.Location
import com.glovo.challenge.model.City
import com.glovo.challenge.repository.CityRepository
import io.mockk.*
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.TestScheduler
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class MainPresenterTest {

    private lateinit var presenter: MainPresenter
    private val repository: CityRepository = mockk(relaxed = true)
    private val view: MainView = mockk(relaxed = true)
    private val scheduler = TestScheduler()
    private val location: Location = mockk(relaxed = true)
    private val city = City(code = "BAR", name = "Barcelona", countryCode = "SP", workingArea = mockk(relaxed = true))

    @Before
    fun setup() {
        presenter = MainPresenter(view, repository, scheduler, scheduler)
        presenter.cityList = mutableListOf(city)
    }

    @Test
    fun onMapReady_requestCountriesAndCities() {
        presenter.onMapReady()
        verify { repository.getCountries() }
        verify { repository.getCities() }
    }

    @Test
    fun onRetryClick_requestCountriesAndCities() {
        presenter.onRetryClick()
        verify { repository.getCountries() }
        verify { repository.getCities() }
    }

    @Test
    fun onMyLocationSuccess_showSelectCityDialog() {
        presenter.onMyLocationSuccess(location)
        verify { view.showSelectCityDialog(any()) }
    }

    @Test
    fun onCitySelected_moveCamera() {
        presenter.onCitySelected("Barcelona")
        verify { view.moveCamera(any()) }
    }

    @Test
    fun onViewDestroyed_dispose() {
        val disposable: Disposable = mockk()
        every { disposable.dispose() } just Runs
        presenter.addDisposable(disposable)
        presenter.onViewDestroyed()
        assertTrue(presenter.disposables.isDisposed)
    }

    @Test
    fun addDisposable_notDisposed() {
        presenter.addDisposable(mockk())
        assertEquals(1, presenter.disposables.size())
        assertFalse(presenter.disposables.isDisposed)
    }
}