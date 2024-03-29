package com.izyver.engenering.sonerimtest

import android.graphics.Point
import com.google.android.gms.maps.Projection
import com.google.android.gms.maps.model.LatLng

data class MarkerPoint(@JvmField val value: Int,@JvmField val latLng: LatLng)

abstract class MarkerProcessor(
    protected val screenSize: Point,
    @JvmField
    protected val markerDiameter: Int
) {
    protected var points: MutableList<MarkerPoint> = mutableListOf()
    open fun generate(upLeft: LatLng, downRight: LatLng) {
        points = MutableList(100) {
            val latLng = LatLng(
                random(upLeft.latitude, downRight.latitude),
                random(upLeft.longitude, downRight.longitude)
            )
            MarkerPoint(random(0.0, 10.0).toInt(), latLng)
        }
    }

    abstract fun getPointsForProjection(projection: Projection, zoom: Float): MutableList<MarkerPoint>

    private fun random(min: Double, max: Double): Double {
        return (Math.random() * (max - min)) + min
    }
}
