package com.example.moldtripapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PlaceAdapter(
    private val places: List<Place>,
    private val onSelectionChanged: (Place, Boolean) -> Unit
) : RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder>() {

    class PlaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(android.R.id.text1)
        val checkBox: CheckBox = itemView.findViewById(android.R.id.checkbox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_multiple_choice, parent, false)
        return PlaceViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        val place = places[position]
        holder.nameTextView.text = place.name
        holder.checkBox.isChecked = place.isSelected
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            onSelectionChanged(place, isChecked)
        }
    }

    override fun getItemCount(): Int = places.size
}