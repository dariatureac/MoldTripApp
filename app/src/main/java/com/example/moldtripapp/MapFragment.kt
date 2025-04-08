package com.example.moldtripapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions

class MapFragment : Fragment(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private var route: Array<ParcelablePlace>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Извлекаем маршрут из аргументов
        route = MapFragmentArgs.fromBundle(requireArguments()).route
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Инициализация карты
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Отображаем маршрут, если он есть
        route?.let { routePoints ->
            if (routePoints.isNotEmpty()) {
                val polylineOptions = PolylineOptions()
                val boundsBuilder = com.google.android.gms.maps.model.LatLngBounds.Builder()

                // Добавляем маркеры и линию маршрута
                routePoints.forEach { place ->
                    val position = LatLng(place.latitude, place.longitude)
                    mMap.addMarker(MarkerOptions().position(position).title(place.name))
                    polylineOptions.add(position)
                    boundsBuilder.include(position)
                }

                mMap.addPolyline(polylineOptions.color(android.graphics.Color.BLUE))
                val bounds = boundsBuilder.build()
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
            }
        }
    }
}