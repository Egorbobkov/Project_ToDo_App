package com.example.myapp2

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class PriorityAdapter(
    context: Context,
    private val resource: Int,
    private val priorities: Array<String>
) : ArrayAdapter<String>(context, resource, priorities) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent) as TextView
        updateTextColor(view, position)
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent) as TextView
        updateTextColor(view, position)
        return view
    }

    private fun updateTextColor(textView: TextView, position: Int) {
        val color = when (priorities[position]) {
            "Высокий!!!" -> Color.parseColor("#FF3B30")
            "Нет", "Средний" -> Color.parseColor("#8E8E93")
            else -> Color.BLACK
        }
        textView.setTextColor(color)
    }
}
