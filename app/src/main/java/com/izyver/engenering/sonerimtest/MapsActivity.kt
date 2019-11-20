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
        mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(51.773911, 25.590464)))
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
        mMarkerProcessor = MarkerProcessorScreeSizeBased(
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
        paint.color = Color.BLACK
        canvas.drawText(text, mMarkerDiameterPx / 3f, mMarkerDiameterPx / 1.3f, paint)
        return bitmap
    }
}