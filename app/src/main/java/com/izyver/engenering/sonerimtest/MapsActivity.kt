package com.izyver.engenering.sonerimtest

import android.graphics.*
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_maps.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnCameraChangeListener {
    private lateinit var mMarkerProcessor: MarkerProcessor
    private lateinit var mMap: GoogleMap

    private var mMarkerDiameterPx: Int = 0
    var mLastZoom = 0f

    override fun onCameraChange(p0: CameraPosition?) {
        p0 ?: return
        if (mLastZoom != p0.zoom) {
            updateMarkers(p0.zoom)
            mLastZoom = p0.zoom
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnCameraChangeListener(this)
        generateNewMarkers()
        val height = window.windowManager.defaultDisplay.height
        val width = window.windowManager.defaultDisplay.width
        val ukraine = LatLngBounds(LatLng(44.1, 22.1), LatLng(52.0, 40.5))
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(ukraine,width, height, 0))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        val mapFragment = SupportMapFragment.newInstance()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, mapFragment)
            .commit()
        mapFragment.getMapAsync(this)

        mMarkerDiameterPx = resources.getDimension(R.dimen.marker_diameter).toInt()
        val screenSize = Point()
        windowManager.defaultDisplay.getSize(screenSize)
        mMarkerProcessor = ImprovedProcessorScreenSizeBased(
            screenSize,
            mMarkerDiameterPx
        )
        fab.setOnClickListener {
            generateNewMarkers()
        }
    }

    private fun generateNewMarkers() {
        mMarkerProcessor.generate(LatLng(51.773911, 25.590464), LatLng(47.057204, 37.210914))
        updateMarkers(mMap.cameraPosition.zoom)
    }

    private var markers: ArrayList<Marker> = arrayListOf()

    private fun updateMarkers(zoom: Float) {
        val markerPoints: MutableList<MarkerPoint> =
            mMarkerProcessor.getPointsForProjection(mMap.projection, zoom)
        markers.forEach { it.remove() }
        markers = ArrayList(markerPoints.size)
        for (point in markerPoints) {
            val markerOptions = MarkerOptions()
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(createMarkerBitmap(point.value.toString())))
            markerOptions.position(point.latLng)
            markers.add(mMap.addMarker(markerOptions))
        }
    }

    private fun createMarkerBitmap(text: String): Bitmap {
        val bitmap =
            Bitmap.createBitmap(mMarkerDiameterPx, mMarkerDiameterPx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint()
        paint.textSize = resources.getDimension(R.dimen.text_size)
        paint.color = Color.RED
        canvas.drawCircle(
            mMarkerDiameterPx / 2f,
            mMarkerDiameterPx / 2f,
            mMarkerDiameterPx / 2f,
            paint
        )
        paint.color = Color.WHITE
        paint.textAlign = Paint.Align.CENTER
        val x = mMarkerDiameterPx / 2f
        val y = (mMarkerDiameterPx / 2f) - ((paint.ascent() + paint.descent()) / 2)
        canvas.drawText(text, x, y, paint)
        return bitmap
    }
}