package com.example.moldtripapp.code
import com.google.android.gms.maps.model.LatLng
import kotlin.math.*

class Algorithm {
    fun calculateDistance(point1: LatLng, point2: LatLng): Double {
        val earthRadius = 6371.0
        val dLat = Math.toRadians(point2.latitude - point1.latitude)
        val dLon = Math.toRadians(point2.longitude - point1.longitude)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(point1.latitude)) * Math.cos(Math.toRadians(point2.latitude)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return earthRadius * c
    }

    // Алгоритм Nearest Neighbor
    private fun nearestNeighbor(places: List<ParcelablePlace>): List<ParcelablePlace> {
        if (places.isEmpty()) return emptyList()

        val route = mutableListOf<ParcelablePlace>()
        val unvisited = places.toMutableList()
        var current = unvisited.removeAt(0)
        route.add(current)

        while (unvisited.isNotEmpty()) {

            val nearest = unvisited.minByOrNull { place ->
                calculateDistance(current.latLng, place.latLng)
            }!!
            current = nearest
            unvisited.remove(nearest)
            route.add(nearest)
        }

        route.add(route[0])
        return route
    }

    private fun twoOpt(route: List<ParcelablePlace>): List<ParcelablePlace> {
        var bestRoute = route.toMutableList()
        var improved = true

        while (improved) {
            improved = false
            for (i in 1 until bestRoute.size - 2) {
                for (j in i + 1 until bestRoute.size - 1) {
                    val oldDistance = calculateDistance(bestRoute[i - 1].latLng, bestRoute[i].latLng) +
                            calculateDistance(bestRoute[j].latLng, bestRoute[j + 1].latLng)
                    val newDistance = calculateDistance(bestRoute[i - 1].latLng, bestRoute[j].latLng) +
                            calculateDistance(bestRoute[i].latLng, bestRoute[j + 1].latLng)

                    if (newDistance < oldDistance) {
                        val newRoute = bestRoute.toMutableList()
                        newRoute.subList(i, j + 1).reverse()
                        bestRoute = newRoute
                        improved = true
                    }
                }
            }
        }

        return bestRoute
    }

    fun findOptimalRoute(places: List<ParcelablePlace>): List<ParcelablePlace> {
        val nnRoute = nearestNeighbor(places)
        return twoOpt(nnRoute)
    }

    fun calculateRouteDistance(route: List<ParcelablePlace>): Double {
        if (route.size < 2) return 0.0
        var totalDistance = 0.0
        for (i in 0 until route.size - 1) {
            totalDistance += calculateDistance(route[i].latLng, route[i + 1].latLng)
        }
        return totalDistance
    }
}