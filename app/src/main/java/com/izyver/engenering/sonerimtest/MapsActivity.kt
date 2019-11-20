package com.izyver.engenering.sonerimtest

import android.graphics.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.gms.maps.*

import com.google.android.gms.maps.model.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnCameraChangeListener {
    private lateinit var markerProcessor: MarkerProcessor
    private var markerDiameter: Int = 0

    var lastZoom = 0f
    override fun onCameraChange(p0: CameraPosition?) {
        p0 ?: return
        Log.d(
            this.javaClass.simpleName,
            "bearing = ${p0.bearing}, (latitude - ${p0.target.latitude}, longitude - ${p0.target.longitude}), tilt = ${p0.tilt}, zoom = ${p0.zoom}"
        )
        if(lastZoom != p0.zoom){
            updateMarkers(p0.zoom)
            lastZoom = p0.zoom
        }
    }

    private lateinit var mMap: GoogleMap

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnCameraChangeListener(this)
        markerProcessor.generate(LatLng(51.773911, 25.590464), LatLng(47.057204, 37.210914))
        updateMarkers(mMap.cameraPosition.zoom)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(51.773911, 25.590464)))
    }

    private var markers: ArrayList<Marker> = arrayListOf()

    private fun updateMarkers(zoom: Float) {
        val points: MutableList<MarkerPoint> = markerProcessor.getPointsForProjection(mMap.projection, zoom)
        markers.forEach { it.remove() }
        markers = ArrayList(points.size)
        for (point in points) {
            val markerOptions = MarkerOptions()
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap(point.value.toString())))
            markerOptions.position(point.latLng)
            markers.add(mMap.addMarker(markerOptions))
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        markerDiameter = resources.getDimension(R.dimen.marker_diameter).toInt()
        val screenSize = Point()
        windowManager.defaultDisplay.getSize(screenSize)
        markerProcessor = MarkerProcessorConvertBased(
            screenSize,
            markerDiameter
        )
    }

    private fun bitmap(text: String): Bitmap {
        val bitmap = Bitmap.createBitmap(markerDiameter, markerDiameter, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint()
        paint.textSize = resources.getDimension(R.dimen.text_size)
        paint.color = Color.RED
        canvas.drawCircle(markerDiameter / 2f, markerDiameter / 2f, markerDiameter / 2f, paint)
        paint.color = Color.BLACK
        canvas.drawText(text, markerDiameter / 3f, markerDiameter / 1.3f, paint)
        return bitmap
    }
}