package com.example.kreonculatorapp.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.kreonculatorapp.LocationTracker
import com.example.kreonculatorapp.R
import com.example.kreonculatorapp.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import org.json.JSONException
import org.json.JSONObject

/**
 * Aktywność obsługująca mapy Google oraz funkcjonalności nawigacji.
 * Pozwala na wybór punktów docelowych oraz ich wyświetlanie na mapie.
 */
class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var placesClient: PlacesClient
    private lateinit var binding: ActivityMapsBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationTracker: LocationTracker
    private var selectedLocation: LatLng? = null

    companion object {
        private const val LOCATION_REQUEST_CODE = 1000
    }

    /**
     * Metoda `onCreate` inicjalizuje widok, klienta lokalizacji i mapę,
     * a także obsługuje przycisk do uruchamiania nawigacji.
     *
     * @param savedInstanceState stan zapisany z poprzednich uruchomień.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, "YOUR_API_KEY")
        }
        placesClient = Places.createClient(this)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        checkLocationPermission()

        locationTracker = LocationTracker(this)

        // Sprawdzenie uprawnień lokalizacji i rozpoczęcie śledzenia
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_REQUEST_CODE
            )
        } else {
            locationTracker.startTracking()
        }

        val startNavigationButton: Button = findViewById(R.id.startNavigationButton)
        startNavigationButton.setOnClickListener {
            selectedLocation?.let {
                startNavigation(it.latitude, it.longitude)
            } ?: run {
                Toast.makeText(this, "Proszę wybrać punkt na mapie, aby rozpocząć nawigację.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Zatrzymuje śledzenie lokalizacji przy zamykaniu aktywności.
     */
    override fun onDestroy() {
        super.onDestroy()
        locationTracker.stopTracking()
    }

    /**
     * Inicjalizuje mapę, kontrolki oraz zdarzenia związane z interakcją użytkownika.
     *
     * @param googleMap Instancja mapy Google.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.setOnMarkerClickListener(this)

        // Nasłuchiwanie kliknięcia na mapie - wybieranie punktu
        mMap.setOnMapClickListener { latLng ->
            // Aktualizuj kliknięte miejsce i dodaj marker
            selectedLocation = latLng
            mMap.clear() // Usuń poprzednie markery
            mMap.addMarker(MarkerOptions().position(latLng).title("Wybrane miejsce"))
            Log.d("MapClick", "Kliknięto: Lat: ${latLng.latitude}, Lng: ${latLng.longitude}")
        }
        setUpMap()
    }

    /**
     * Konfiguracja mapy: sprawdza uprawnienia lokalizacji,
     * włącza lokalizację użytkownika na mapie i dodaje marker w bieżącej lokalizacji.
     */
    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_REQUEST_CODE
            )
            return
        }

        // Włącz funkcję lokalizacji użytkownika na mapie
        mMap.isMyLocationEnabled = true

        // Pobierz ostatnią znaną lokalizację użytkownika
        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            if (location != null) {
                val currentLatLong = LatLng(location.latitude, location.longitude)
                placeMarkerOnMap(currentLatLong)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong, 11f))
            }
        }
    }

    /**
     * Dodaje marker na mapie w zadanym punkcie.
     *
     * @param currentLatLong Współrzędne punktu docelowego.
     */
    private fun placeMarkerOnMap(currentLatLong: LatLng) {
        val markerOptions = MarkerOptions().position(currentLatLong)
        markerOptions.title("$currentLatLong")
        mMap.addMarker(markerOptions)
    }

    /**
     * Uruchamia aplikację Google Maps, aby rozpocząć nawigację do podanej lokalizacji.
     *
     * @param destinationLatitude Szerokość geograficzna miejsca docelowego.
     * @param destinationLongitude Długość geograficzna miejsca docelowego.
     */
    private fun startNavigation(destinationLatitude: Double, destinationLongitude: Double) {
        val uri = "google.navigation:q=$destinationLatitude,$destinationLongitude"
        val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(uri)).apply {
            setPackage("com.google.android.apps.maps")
        }

        // Sprawdzenie, czy aplikacja Google Maps jest dostępna
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Log.d("Navigation", "Nie można uruchomić aplikacji Google Maps.")
        }
    }

    /**
     * Metoda `onMarkerClick` jest wywoływana po kliknięciu markera.
     * W tej aplikacji kliknięcia markerów nie są specjalnie obsługiwane, więc zwraca `false`.
     *
     * @param p0 marker, który został kliknięty.
     * @return `false`, ponieważ nie jest konieczna specjalna obsługa kliknięcia.
     */
    override fun onMarkerClick(p0: Marker) = false

    /**
     * Sprawdza uprawnienia lokalizacji. Jeśli nie są one przyznane, prosi użytkownika o ich udzielenie.
     * Gdy uprawnienia są już przyznane, pobiera ostatnią znaną lokalizację użytkownika.
     */
    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_REQUEST_CODE
            )
        } else {
            getLastKnownLocation()
        }
    }

    /**
     * Metoda wywoływana po wyniku żądania uprawnień.
     * Sprawdza, czy użytkownik przyznał uprawnienia lokalizacyjne.
     * Jeśli uprawnienia są przyznane, rozpoczyna śledzenie lokalizacji.
     *
     * @param requestCode kod żądania uprawnień.
     * @param permissions tablica żądanych uprawnień.
     * @param grantResults tablica wyników odpowiadających uprawnieniom.
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationTracker.startTracking()
            } else {
                Log.d("MainActivity", "Uprawnienia lokalizacyjne nie zostały przyznane.")
            }
        }
    }

    /**
     * Pobiera ostatnią znaną lokalizację użytkownika, jeśli uprawnienia zostały przyznane.
     * Jeśli uprawnienia nie zostały przyznane, wyświetla odpowiedni komunikat.
     */
    private fun getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("Location", "Permission not granted")
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                Log.d("Location", "Lat: ${it.latitude}, Lng: ${it.longitude}")
            }
        }
    }

    /**
     * Wyszukuje pobliskie szpitale za pomocą Google Places API i dodaje markery dla każdego
     * znalezionego miejsca.
     *
     * @param location Bieżąca lokalizacja użytkownika.
     */
    private fun findNearbyHospitals(location: LatLng) {
        // Pobierz klucz API zapisany w pliku local.properties (przez R.string)
        val apiKey = getString(R.string.google_api_key)
        val locationString = "${location.latitude},${location.longitude}"
        val radius = 50000  // Promień wyszukiwania w metrach
        val type = "hospital"
        val url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=$locationString&radius=$radius&type=$type&key=$apiKey"

        // Utwórz żądanie HTTP do API
        val request = object : StringRequest(
            Method.GET, url,
            Response.Listener { response ->
                try {
                    // Parsuj odpowiedź JSON
                    val jsonObject = JSONObject(response)
                    val results = jsonObject.getJSONArray("results")

                    // Iteracja po wynikach i dodanie markerów na mapie
                    for (i in 0 until results.length()) {
                        val place = results.getJSONObject(i)
                        val latLng = place.getJSONObject("geometry")
                            .getJSONObject("location")
                        val lat = latLng.getDouble("lat")
                        val lng = latLng.getDouble("lng")
                        val placeName = place.getString("name")

                        // Dodaj marker dla każdego miejsca na mapie
                        mMap.addMarker(
                            MarkerOptions()
                                .position(LatLng(lat, lng))
                                .title(placeName)
                        )
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                // Obsługa błędów
                Log.e("MapsActivity", "Błąd podczas wyszukiwania: ${error.message}")
            }) {}

        // Dodaj żądanie do kolejki
        Volley.newRequestQueue(this).add(request)
    }

}
