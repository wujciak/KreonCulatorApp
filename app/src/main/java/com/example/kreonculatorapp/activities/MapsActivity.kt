package com.example.kreonculatorapp.activities

import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.kreonculatorapp.LocationTracker
import com.example.kreonculatorapp.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var locationTracker: LocationTracker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        // Inicjalizacja mapy
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Inicjalizacja LocationTracker
        locationTracker = LocationTracker(this)
        locationTracker.startTracking()

        // Obserwacja zmiany lokalizacji
        locationTracker.location.observe(this, Observer { location ->
            val currentLocation = LatLng(location.latitude, location.longitude)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f))
            mMap.addMarker(MarkerOptions().position(currentLocation).title("Twoja lokalizacja"))
        })
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        // Konfiguracja mapy
        mMap.uiSettings.isZoomControlsEnabled = true

        // Wywołanie wyszukiwania najbliższych toalet po kliknięciu przycisku wyszukiwania
        findViewById<EditText>(R.id.searchBar).setOnClickListener {
            searchNearbyPlaces("public toilet")
        }
    }

    private fun searchNearbyPlaces(query: String) {
        // Implementacja wyszukiwania miejsc (użycie API Places lub innego)
        // To miejsce, gdzie integrujesz wyszukiwanie z Google Places API lub innym rozwiązaniem
    }

    override fun onDestroy() {
        super.onDestroy()
        locationTracker.stopTracking()
    }
}
