package com.glovo.challenge.ui

import android.widget.AdapterView

interface DefaultOnItemSelectedListener : AdapterView.OnItemSelectedListener {
    override fun onNothingSelected(parent: AdapterView<*>?) {}
}