package com.example.moldtripapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moldtrippapp.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.button.MaterialButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.text.format
import kotlin.toString

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Настройка NavController
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}

class FavoritesFragment : Fragment() {
    private lateinit var placeAdapter: PlaceAdapter
    private lateinit var places: MutableList<Place>
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val algorithm = Algorithm()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Инициализация Firebase Realtime Database
        val database = FirebaseDatabase.getInstance("https://pblme1-default-rtdb.europe-west1.firebasedatabase.app/")
        val locationsRef = database.reference.child("locations")

        // Извлекаем данные из Firebase
        locationsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                places = mutableListOf()
                for (placeSnapshot in snapshot.children) {
                    val place = placeSnapshot.getValue(Place::class.java)
                    place?.let {
                        it.id = placeSnapshot.key?.toLong() ?: 0 // Устанавливаем ID из ключа узла
                        places.add(it)
                    }
                }

                if (places.isEmpty()) {
                    Toast.makeText(context, "Список мест пуст", Toast.LENGTH_LONG).show()
                    Log.d("FirebaseData", "Список мест пуст")
                    return
                }

                // Логируем данные для проверки
                places.forEach { place ->
                    Log.d("FirebaseData", "Место: ${place.name}, Координаты: (${place.latitude}, ${place.longitude}), Избрано: ${place.isSelected}")
                }

                // Настройка RecyclerView
                val recyclerView = view.findViewById<RecyclerView>(R.id.placesRecyclerView)
                recyclerView.layoutManager = LinearLayoutManager(context)
                placeAdapter = PlaceAdapter(places) { place, isSelected ->
                    // Обновляем isSelected в Firebase
                    place.isSelected = isSelected
                    locationsRef.child(place.id.toString()).child("isSelected").setValue(isSelected)
                }
                recyclerView.adapter = placeAdapter

                val routeTextView = view.findViewById<TextView>(R.id.routeTextView)

                view.findViewById<MaterialButton>(R.id.buildRouteButton).setOnClickListener {
                    val selectedPlaces = places.filter { it.isSelected }.map {
                        ParcelablePlace(it.name, it.latitude, it.longitude)
                    }

                    if (selectedPlaces.isEmpty()) {
                        Toast.makeText(context, "Выберите хотя бы одно место", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    if (ActivityCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            requireActivity(),
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            1
                        )
                        return@setOnClickListener
                    }

                    val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
                    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        Toast.makeText(context, "Пожалуйста, включите GPS", Toast.LENGTH_LONG).show()
                        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        startActivity(intent)
                        return@setOnClickListener
                    }

                    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                        if (location != null) {
                            val currentLocation = ParcelablePlace(
                                "Ваше местоположение",
                                location.latitude,
                                location.longitude
                            )

                            val nearestPlace = selectedPlaces.minByOrNull { place ->
                                algorithm.calculateDistance(currentLocation, place)
                            } ?: throw IllegalStateException("Не удалось найти ближайшее место")

                            val placesForRoute = mutableListOf<ParcelablePlace>()
                            placesForRoute.add(nearestPlace)
                            placesForRoute.addAll(selectedPlaces.filter { it != nearestPlace })

                            val optimalRoute = algorithm.findOptimalRoute(placesForRoute, nearestPlace)
                            val totalDistance = algorithm.calculateRouteDistance(optimalRoute)

                            routeTextView.text = "Маршрут: ${optimalRoute.joinToString(" -> ") { it.name }}\nДлина: ${"%.2f".format(totalDistance)} км"
                            routeTextView.visibility = View.VISIBLE

                            // Передаём маршрут в MapFragment
                            val action = FavoritesFragmentDirections.actionFavoritesFragmentToMapFragment(optimalRoute.toTypedArray())
                            findNavController().navigate(action)
                        } else {
                            Toast.makeText(
                                context,
                                "Не удалось определить местоположение. Проверьте настройки GPS и интернет.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }.addOnFailureListener { e ->
                        Toast.makeText(
                            context,
                            "Ошибка получения местоположения: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Ошибка чтения данных: ${error.message}", Toast.LENGTH_LONG).show()
                Log.e("FirebaseData", "Ошибка чтения данных: ${error.message}")
            }
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            view?.findViewById<MaterialButton>(R.id.buildRouteButton)?.performClick()
        } else {
            Toast.makeText(
                context,
                "Разрешение на доступ к местоположению не предоставлено",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}