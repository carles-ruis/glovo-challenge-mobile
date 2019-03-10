package com.glovo.challenge.ui.city

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import com.glovo.challenge.R
import com.glovo.challenge.ui.DefaultOnItemSelectedListener
import kotlinx.android.synthetic.main.dialog_select_city.*
import kotlinx.android.synthetic.main.dialog_select_city.view.*

class SelectCityDialog(context: Context, val countriesCities: Map<String, List<String>>, val onCitySelected : (String) -> Unit)
    : AlertDialog(context) {

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
            countriesAdapter.setItems(countriesCities.keys.toList())
            view.dialog_select_country_spinner.adapter = countriesAdapter

            citiesAdapter = HintSpinnerAdapter(context, context.getString(R.string.select_city_city_hint))
            view.dialog_select_city_spinner.adapter = citiesAdapter

            okButton = getButton(BUTTON_POSITIVE)
            okButton.setEnabled(false)

            view.dialog_select_country_spinner.onItemSelectedListener = object : DefaultOnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    onCountrySelected(parent!!.getItemAtPosition(position) as String)
                    updateOkButton()
                }
            }
            view.dialog_select_city_spinner.onItemSelectedListener = object : DefaultOnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    updateOkButton()
                }
            }
        }

        super.onCreate(savedInstanceState)
    }

    private fun onCountrySelected(country:String) {
        dialog_select_city_spinner.setSelection(0)
        citiesAdapter.setItems(countriesCities.get(country) ?: emptyList())
    }

    private fun updateOkButton() {
        okButton.setEnabled(dialog_select_city_spinner.selectedItemPosition != 0 && dialog_select_country_spinner.selectedItemPosition != 0)
    }

    private fun onOkClick() {
        onCitySelected(dialog_select_city_spinner.selectedItem as String)
    }

}