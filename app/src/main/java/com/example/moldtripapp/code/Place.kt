package com.example.moldtripapp

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Place(
    var id: Long = 0,
    var name: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var isSelected: Boolean = false
)