package com.izyver.engenering.sonerimtest

import android.graphics.Point
import com.google.android.gms.maps.Projection
import com.google.android.gms.maps.model.LatLng
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

data class MarkerPoint(val value: Int, val latLng: LatLng)

class MarkerProcessor(
    private val screenSize: Point,
    private val markerDiameter: Int
) {
    private var points: MutableList<MarkerPoint> = mutableListOf()
    fun generate(upLeft: LatLng, downRight: LatLng) {
        var i = 0
        points = MutableList(20) {
            val latLng = LatLng(
                random(upLeft.latitude, downRight.latitude),
                random(upLeft.longitude, downRight.longitude)
            )
            MarkerPoint(/*random(0.0, 10.0).toInt()*/i++, latLng)
        }
    }

    fun getPointsForProjection(projection: Projection): MutableList<MarkerPoint> {
        val result = mutableListOf<MarkerPoint>()
        val diffLongitude = abs(
            projection.visibleRegion.farLeft.longitude - projection.visibleRegion.farRight.longitude
        )
        val markerRationDiameter = diffLongitude * markerDiameter / screenSize.x

        for (point in points) {
            if (!isInProjection(point, projection)) continue
            val intersections = getIntersections(point, points, markerRationDiameter)
            if (intersections.isEmpty()) {
                result.add(point)
            } else {
                intersections.add(point)
                result.addAll(split(intersections, markerRationDiameter))
            }
        }
        return result
    }

    private fun split(
        intersections: MutableList<MarkerPoint>,
        markerRationDiameter: Double
    ): MutableList<MarkerPoint> {
        var i = 0
        while (i != intersections.lastIndex) {
            for (point in intersections) {
                if (length(point, intersections[i]) > markerRationDiameter) {
                    val newPoint = MarkerPoint(
                        (point.value + intersections[i].value) / 2,
                        averagePoint(intersections[i], point)
                    )
                    intersections[i] = newPoint
                    intersections.remove(point)
                } else {
                    i++
                }
            }
        }
        return intersections
    }

    private fun averagePoint(point1: MarkerPoint, point2: MarkerPoint): LatLng {
        return LatLng(
            (point1.latLng.latitude + point2.latLng.latitude) / 2,
            (point1.latLng.longitude + point2.latLng.longitude) / 2
        )
    }

    private fun getIntersections(
        point: MarkerPoint,
        points: List<MarkerPoint>,
        markerRationDiameter: Double
    ): MutableList<MarkerPoint> {
        val intersections = mutableListOf<MarkerPoint>()

        for (otherPoint in points) {
            val length = length(point, otherPoint)
            if (length > markerRationDiameter) {
                continue
            } else {
                intersections.add(otherPoint)
            }
        }
        intersections.size.dec()
        return intersections
    }

    private fun length(point1: MarkerPoint, point2: MarkerPoint): Double {
        val latLng1 = point1.latLng
        val latLng2 = point2.latLng
        if (latLng1 == latLng2) return Double.MAX_VALUE
        val first = latLng1.longitude - latLng1.latitude
        val second = latLng2.longitude - latLng2.latitude
        return sqrt(first.pow(2) + second.pow(2))
    }

    private fun isInProjection(point: MarkerPoint, projection: Projection): Boolean {
        return point.latLng.longitude > projection.visibleRegion.farLeft.longitude
                && point.latLng.longitude < projection.visibleRegion.farRight.longitude
                && point.latLng.latitude < projection.visibleRegion.farLeft.latitude
                && point.latLng.latitude > projection.visibleRegion.nearLeft.latitude
    }

    private fun random(min: Double, max: Double): Double {
        max - min
        return (Math.random() * (max - min)) + min
    }
}
