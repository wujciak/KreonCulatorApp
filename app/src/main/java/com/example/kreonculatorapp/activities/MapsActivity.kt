package com.example.kreonculatorapp.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import android.widget.EditText
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
     * Metoda onCreate inicjalizuje widok, klienta lokalizacji i mapę,
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

        val searchEditText: EditText = findViewById(R.id.searchEditText)
        val searchButton: Button = findViewById(R.id.searchButton)

        searchButton.setOnClickListener {
            val query = searchEditText.text.toString().trim()
            if (query.isNotEmpty()) {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        val currentLatLong = LatLng(location.latitude, location.longitude)
                        findNearbyPlaces(currentLatLong, query)
                    } else {
                        Toast.makeText(this, "Nie udało się pobrać lokalizacji. Sprawdź uprawnienia.", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Proszę wpisać frazę do wyszukania.", Toast.LENGTH_SHORT).show()
            }
        }

        val hospitalButton: Button = findViewById(R.id.findHospitalsButton)
        hospitalButton.setOnClickListener {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val currentLatLong = LatLng(location.latitude, location.longitude)
                    findNearbyPlaces(currentLatLong, "hospital")
                } else {
                    Toast.makeText(this, "Nie udało się pobrać lokalizacji. Sprawdź uprawnienia.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        val findToiletsButton: Button = findViewById(R.id.findToiletsButton)
        findToiletsButton.setOnClickListener {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val currentLatLong = LatLng(location.latitude, location.longitude)
                    findNearbyPlaces(currentLatLong, "toilet")
                } else {
                    Toast.makeText(this, "Nie udało się pobrać lokalizacji. Sprawdź uprawnienia.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        val findPharmaciesButton: Button = findViewById(R.id.findPharmaciesButton)
        findPharmaciesButton.setOnClickListener {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val currentLatLong = LatLng(location.latitude, location.longitude)
                    findNearbyPlaces(currentLatLong, "pharmacy")
                } else {
                    Toast.makeText(this, "Nie udało się pobrać lokalizacji. Sprawdź uprawnienia.", Toast.LENGTH_SHORT).show()
                }
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
     * Metoda onMarkerClick jest wywoływana po kliknięciu markera.
     * W tej aplikacji kliknięcia markerów nie są specjalnie obsługiwane, więc zwraca false.
     *
     * @param p0 marker, który został kliknięty.
     * @return false, ponieważ nie jest konieczna specjalna obsługa kliknięcia.
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

    private fun getApiKey(): String? {
        return try {
            val ai = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
            ai.metaData.getString("com.google.android.geo.API_KEY")
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            null
        }
    }

    private fun findNearbyPlaces(location: LatLng, query: String) {
        val apiKey = getApiKey() ?: run {
            Log.e("MapsActivity", "API Key not found!")
            return
        }

        val locationString = "${location.latitude},${location.longitude}"
        val radius = 5000 // Promień wyszukiwania w metrach
        val url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=$locationString&radius=$radius&keyword=$query&key=$apiKey"

        val request = object : StringRequest(
            Method.GET, url,
            Response.Listener { response ->
                Log.d("API Response", response)
                try {
                    val jsonResponse = JSONObject(response)
                    val results = jsonResponse.getJSONArray("results")

                    mMap.clear() // Czyść mapę z poprzednich markerów

                    for (i in 0 until results.length()) {
                        val place = results.getJSONObject(i)
                        val loc = place.getJSONObject("geometry").getJSONObject("location")
                        val lat = loc.getDouble("lat")
                        val lng = loc.getDouble("lng")
                        val name = place.getString("name")

                        val markerOptions = MarkerOptions()
                            .position(LatLng(lat, lng))
                            .title(name)
                        mMap.addMarker(markerOptions)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Błąd podczas przetwarzania wyników wyszukiwania.", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error ->
                Log.e("API Error", error.toString())
                Toast.makeText(this, "Nie udało się pobrać wyników wyszukiwania.", Toast.LENGTH_SHORT).show()
            }
        ) {}

        Volley.newRequestQueue(this).add(request)
    }


}
