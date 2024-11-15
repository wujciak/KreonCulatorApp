package com.example.kreonculatorapp.activities

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.kreonculatorapp.LocationTracker
import com.example.kreonculatorapp.NearbyPlacesTask
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
            val query = "public toilet"
            searchNearbyPlaces(query)
        }
    }

    private fun searchNearbyPlaces(query: String) {
        val currentLocation = locationTracker.location.value
        if (currentLocation != null) {
            val url = buildPlacesUrl(currentLocation.latitude, currentLocation.longitude, query)
            NearbyPlacesTask(mMap).execute(url)
        } else {
            Toast.makeText(this, "Nie udało się uzyskać lokalizacji", Toast.LENGTH_SHORT).show()
        }
    }

    private fun buildPlacesUrl(latitude: Double, longitude: Double, query: String): String {
        val apiKey = getString(R.string.google_api_key)  // Dodaj swój klucz API w pliku strings.xml
        return "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=$latitude,$longitude&radius=1500&type=$query&key=$apiKey"
    }

    override fun onDestroy() {
        super.onDestroy()
        locationTracker.stopTracking()
    }
}
