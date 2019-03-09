package com.glovo.challenge.ui

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import com.glovo.challenge.R
import kotlinx.android.synthetic.main.dialog_select_city.view.*

class SelectCityDialog(context: Context, val countryList: List<String>, val cityList: List<String>) : AlertDialog(context) {

    private lateinit var countriesAdapter: HintSpinnerAdapter
    private lateinit var citiesAdapter: HintSpinnerAdapter
    private lateinit var okButton: View

    override fun onCreate(savedInstanceState: Bundle?) {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_select_city, null)
        setView(view)
        setTitle(R.string.select_city_title)
        setCancelable(false)
        setButton(BUTTON_POSITIVE, context.getString(R.string.ok)) { _, _ -> onOkClick() }

        setOnShowListener {
            countriesAdapter = HintSpinnerAdapter(context, context.getString(R.string.select_city_country_hint))
            countriesAdapter.setItems(countryList)
            view.dialog_select_country_spinner.adapter = countriesAdapter

            citiesAdapter = HintSpinnerAdapter(context, context.getString(R.string.select_city_city_hint))
            view.dialog_select_city_spinner.adapter = citiesAdapter


            okButton = getButton(BUTTON_POSITIVE)
            okButton.setEnabled(false)
        }

        super.onCreate(savedInstanceState)
    }

    private fun onOkClick() {}
}