@file:Suppress("DEPRECATION")

package com.example.kreonculatorapp.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationRequest
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.kreonculatorapp.LocationTracker
import com.example.kreonculatorapp.R

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.kreonculatorapp.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.Marker
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

@Suppress("DEPRECATION")
class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var placesClient: PlacesClient
    private lateinit var binding: ActivityMapsBinding
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationTracker: LocationTracker

    companion object {
        private const val LOCATION_REQUEST_CODE = 1000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!Places.isInitialized()) {
            val apiKey = getString(R.string.google_api_key)
            Places.initialize(applicationContext, apiKey)

        }
        // Initialize the Places API client
        placesClient = Places.createClient(this)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Inicjalizacja klienta lokalizacji, który pobiera dane lokalizacyjne użytkownika
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
//        locationTracker = LocationTracker(this)
//
//        // Sprawdzenie i uzyskanie uprawnień lokalizacji przed uruchomieniem śledzenia
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE)
//        } else {
//            // Rozpocznij śledzenie lokalizacji
//            locationTracker.startTracking()
//        }
//    }

//    override fun onDestroy() {
//        super.onDestroy()
//        // Zatrzymaj śledzenie lokalizacji, gdy Activity jest niszczona
//        locationTracker.stopTracking()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.setOnMarkerClickListener(this)
        setUpMap()
    }

    private fun setUpMap() {
        // Sprawdź, czy aplikacja ma uprawnienia do uzyskiwania lokalizacji
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Jeśli nie ma uprawnień, żądaj ich od użytkownika
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
                // Zapisz ostatnią lokalizację użytkownika
                lastLocation = location

                // Stwórz obiekt LatLng z aktualnej lokalizacji
                val currentLatLong = LatLng(location.latitude, location.longitude)

                // Dodaj marker na mapie w bieżącej lokalizacji
                placeMarkerOnMap(currentLatLong)

                // Przesuń kamerę mapy do bieżącej lokalizacji
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong, 11f))
                val searchBar = findViewById<EditText>(R.id.searchBar)
                searchBar.setOnEditorActionListener { _, _, _ ->
                    val query = searchBar.text.toString()
                    searchNearbyPlaces(query,currentLatLong)
                    true
                }
            }
        }
    }

    private fun searchNearbyPlaces(query: String, location: LatLng) {
        val apiKey = getString(R.string.google_api_key)
        val urlString =
            "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=${location.latitude},${location.longitude}&radius=5000&type=$query&key=$apiKey"

        // Uruchomienie asynchronicznego zadania do wykonania zapytania do Google Places API
        NearbyPlacesTask().execute(urlString)
    }


    @SuppressLint("StaticFieldLeak")
    inner class NearbyPlacesTask : AsyncTask<String, Void, String>() {

        @Deprecated("Deprecated in Java")
        override fun doInBackground(vararg params: String?): String? {
            val urlString = params[0] // URL zapytania API
            var response: String? = null

            try {
                val url = URL(urlString)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                val inputStream = connection.inputStream
                response = inputStream.bufferedReader().use { it.readText() }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return response
        }

        @Deprecated("Deprecated in Java")
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            if (result != null) {
                // Jeśli wynik jest niepusty, przetwórz dane
                parseNearbyPlaces(result)
            } else {
                // Wyświetl komunikat o nieudanym pobraniu danych
                Toast.makeText(this@MapsActivity, "Nie udało się pobrać miejsc", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Funkcja do przetwarzania wyniku JSON z Google Places API.
     * Odczytuje odpowiedź, analizuje listę miejsc i dodaje markery na mapie dla każdego znalezionego miejsca.
     *
     * @param result Wynik zapytania w formacie JSON (ciąg znaków).
     */
    private fun parseNearbyPlaces(result: String) {
        try {
            // Wyczyszczenie mapy z poprzednich markerów
            mMap.clear()

            // Konwersja wyniku do obiektu JSON
            val jsonObject = JSONObject(result)
            val resultsArray = jsonObject.getJSONArray("results") // Tablica wyników miejsc

            // Iteracja po wszystkich miejscach w odpowiedzi
            for (i in 0 until resultsArray.length()) {
                val placeObject = resultsArray.getJSONObject(i) // Pojedyncze miejsce
                val placeName = placeObject.getString("name") // Nazwa miejsca

                // Pobranie współrzędnych geograficznych miejsca
                val geometry = placeObject.getJSONObject("geometry")
                val location = geometry.getJSONObject("location")
                val lat = location.getDouble("lat")
                val lng = location.getDouble("lng")

                // Tworzenie obiektu LatLng na podstawie współrzędnych
                val placeLatLng = LatLng(lat, lng)

                // Dodanie markera na mapie dla tego miejsca
                mMap.addMarker(MarkerOptions().position(placeLatLng).title(placeName))
            }
        } catch (e: Exception) {
            e.printStackTrace() // Wypisanie błędu, jeśli JSON nie mógł zostać poprawnie przetworzony
        }
    }


    private fun placeMarkerOnMap(currentLatLong: LatLng) {
        // Stwórz opcje dla markera
        val markerOptions = MarkerOptions().position(currentLatLong)
        markerOptions.title("$currentLatLong")

        // Dodaj marker na mapie
        mMap.addMarker(markerOptions)
    }

    override fun onMarkerClick(p0: Marker) = false

//    private fun checkLocationPermission() {
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
//                LOCATION_REQUEST_CODE
//            )
//        } else {
//            // Gdy uprawnienia są już przyznane, możemy od razu uzyskać lokalizację
//            getLastKnownLocation()
//        }
//    }
//
//    /**
//     * Metoda wywoływana po wyniku żądania uprawnień.
//     * Sprawdza, czy użytkownik przyznał uprawnienia lokalizacyjne.
//     * Jeśli uprawnienia są przyznane, rozpoczyna śledzenie lokalizacji.
//     * Jeśli uprawnienia są odmówione, loguje odpowiedni komunikat.
//     *
//     * @param requestCode Kod żądania, identyfikujący żądanie uprawnień.
//     * @param permissions Tablica uprawnień żądanych przez aplikację.
//     * @param grantResults Tablica wyników dla odpowiadających im uprawnień.
//     */
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == LOCATION_REQUEST_CODE) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                locationTracker.startTracking() // Rozpocznij śledzenie, jeśli uzyskano uprawnienia
//            } else {
//                Log.d("MainActivity", "Uprawnienia lokalizacyjne nie zostały przyznane.")
//            }
//        }
//    }
//
//    /**
//     * Pobiera ostatnią znaną lokalizację użytkownika, jeśli przyznano odpowiednie uprawnienia.
//     * Jeśli uprawnienie nie zostało przyznane, loguje komunikat i kończy działanie metody.
//     * Jeśli uprawnienie jest przyznane, uzyskuje ostatnią lokalizację i loguje współrzędne.
//     */
//    private fun getLastKnownLocation() {
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // Jeśli uprawnienie nie jest przyznane, zakończ wywołanie tej metody.
//            Log.d("Location", "Permission not granted")
//            return
//        }
//        // Jeśli uprawnienie jest przyznane, uzyskaj lokalizację
//        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
//            location?.let {
//                // Przykład: wyświetlenie współrzędnych
//                Log.d("Location", "Lat: ${it.latitude}, Lng: ${it.longitude}")
//            }
//        }
//    }
//
//    private lateinit var locationCallback: LocationCallback
//
//    /**
//     * Inicjalizuje cykliczne aktualizacje lokalizacji użytkownika z ustalonym interwałem oraz priorytetem.
//     * Tworzy i konfiguruje LocationRequest, aby odbierać aktualizacje o wysokiej dokładności.
//     * Metoda sprawdza, czy aplikacja posiada uprawnienia lokalizacyjne przed rozpoczęciem aktualizacji.
//     * W przypadku braku uprawnień loguje odpowiedni komunikat.
//     */
//    private fun startLocationUpdates() {
//        val locationRequest = com.google.android.gms.location.LocationRequest.Builder(
//            Priority.PRIORITY_HIGH_ACCURACY, // Ustalanie priorytetu
//            10000 // Interwał w milisekundach
//        ).apply {
//            setMinUpdateIntervalMillis(5000) // Najkrótszy czas odświeżenia w milisekundach
//            setPriority(Priority.PRIORITY_HIGH_ACCURACY)
//        }.build()
//        locationCallback = object : LocationCallback() {
//            override fun onLocationResult(locationResult: LocationResult) {
//                for (location in locationResult.locations) {
//                    // Użyj lokalizacji, np. do aktualizacji interfejsu
//                    Log.d("Location Update", "Lat: ${location.latitude}, Lng: ${location.longitude}")
//                }
//            }
//        }
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
//        } else {
//            // Obsługa przypadku, gdy uprawnienie nie jest przyznane
//            Log.d("Location Update", "Permission not granted")
//        }
//    }
//    /**
//     * Zatrzymuje aktualizacje lokalizacji użytkownika, gdy aktywność przechodzi w stan wstrzymania.
//     * Usuwa aktualizacje lokalizacji, aby oszczędzać baterię.
//     */
//    override fun onPause() {
//        super.onPause()
//        fusedLocationClient.removeLocationUpdates(locationCallback)
//    }
//

}