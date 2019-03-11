package com.glovo.challenge.ui.city

import android.content.Context
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.glovo.challenge.R

class HintSpinnerAdapter(context: Context, val firstItem: String)
    : ArrayAdapter<String>(context, R.layout.item_hint_spinner, mutableListOf(firstItem)) {

    private val hintTextColor = ContextCompat.getColor(context, R.color.hintTextColor)
    private val defaultTextColor = ContextCompat.getColor(context, R.color.defaultTextColor)

    override fun isEnabled(position: Int) = position > 0

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup) =
            super.getDropDownView(position, convertView, parent).apply {
                val textView = this as TextView
                textView.setTextColor(if (position == 0) hintTextColor else defaultTextColor)
            }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup) =
            super.getView(position, convertView, parent).apply {
                val textView = this as TextView
                textView.setTextColor(if (position == 0) hintTextColor else defaultTextColor)
            }

    fun setItems(items: List<String>) {
        clear()
        add(firstItem)
        addAll(items)
    }
}
