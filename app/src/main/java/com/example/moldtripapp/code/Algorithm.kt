package com.example.moldtripapp

import kotlin.math.*

/**
 * Класс для работы с маршрутами: вычисление расстояний, построение оптимального маршрута
 * и расчёт общей длины маршрута.
 */
class Algorithm {

    // Радиус Земли в километрах
    private val EARTH_RADIUS = 6371.0

    /**
     * Вычисляет расстояние между двумя точками на поверхности Земли (в километрах)
     * с использованием формулы гаверсинусов.
     *
     * @param point1 Первая точка (широта и долгота).
     * @param point2 Вторая точка (широта и долгота).
     * @return Расстояние между точками в километрах.
     */
    fun calculateDistance(point1: ParcelablePlace, point2: ParcelablePlace): Double {
        val dLat = Math.toRadians(point2.latitude - point1.latitude)
        val dLon = Math.toRadians(point2.longitude - point1.longitude)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(point1.latitude)) * cos(Math.toRadians(point2.latitude)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return EARTH_RADIUS * c
    }

    /**
     * Находит оптимальный маршрут, используя алгоритм ближайшего соседа.
     * Начинает с начальной точки (start) и добавляет ближайшие точки, пока не посетит все.
     *
     * @param places Список точек для построения маршрута.
     * @param start Начальная точка маршрута.
     * @return Список точек в порядке оптимального маршрута.
     */
    fun findOptimalRoute(places: List<ParcelablePlace>, start: ParcelablePlace): List<ParcelablePlace> {
        if (places.isEmpty()) return emptyList()

        val unvisited = places.toMutableList()
        val route = mutableListOf<ParcelablePlace>()
        var current = start

        route.add(current)
        unvisited.remove(current)

        while (unvisited.isNotEmpty()) {
            val nearest = unvisited.minByOrNull { calculateDistance(current, it) }
            if (nearest == null) {
                // Это не должно произойти, так как мы проверяем unvisited.isNotEmpty(),
                // но добавляем для безопасности
                break
            }
            route.add(nearest)
            current = nearest
            unvisited.remove(nearest)
        }

        return route
    }

    /**
     * Вычисляет общую длину маршрута в километрах.
     *
     * @param route Список точек маршрута.
     * @return Общая длина маршрута в километрах.
     */
    fun calculateRouteDistance(route: List<ParcelablePlace>): Double {
        if (route.size < 2) return 0.0

        var totalDistance = 0.0
        for (i in 0 until route.size - 1) {
            totalDistance += calculateDistance(route[i], route[i + 1])
        }
        return totalDistance
    }
}