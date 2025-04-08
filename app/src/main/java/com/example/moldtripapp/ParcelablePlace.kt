package com.example.moldtripapp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ParcelablePlace(
    val name: String,
    val latitude: Double,
    val longitude: Double
) : Parcelable