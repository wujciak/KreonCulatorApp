package com.example.kreonculatorapp.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

/**
 * Klasa zarządzająca procesem śledzenia lokalizacji użytkownika.
 * Obsługuje pobieranie danych GPS oraz ich przetwarzanie w czasie rzeczywistym.
 *
 * @param context Kontekst aplikacji do zarządzania usługami systemowymi.
 */
class LocationTracker(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    private lateinit var locationCallback: LocationCallback

    /**
     * Rozpoczyna śledzenie lokalizacji użytkownika z określonym interwałem i poziomem dokładności.
     * Metoda sprawdza, czy wymagane uprawnienia do lokalizacji są przyznane.
     * Jeśli uprawnienia są przyznane, inicjuje aktualizacje lokalizacji i loguje każdą aktualizację.
     * Jeśli uprawnienia nie są przyznane, loguje odpowiedni komunikat i nie rozpoczyna śledzenia.
     */
    fun startTracking() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("LocationTracker", "Brak uprawnień lokalizacyjnych.")
            return
        }

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            10000L // Interwał odświeżania lokalizacji w milisekundach
        ).apply {
            setMinUpdateIntervalMillis(5000L) // Minimalny czas między aktualizacjami lokalizacji w milisekundach
        }.build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    Log.d("Location Update", "Lat: ${location.latitude}, Lng: ${location.longitude}")
                    sendLocationToDatabase(location.latitude, location.longitude)
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    /**
     * Zatrzymuje śledzenie lokalizacji użytkownika poprzez usunięcie aktualizacji lokalizacji z fusedLocationClient.
     * Metoda ta jest przydatna do zakończenia śledzenia lokalizacji w tle, co pozwala oszczędzać baterię.
     */
    fun stopTracking() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        Log.d("LocationTracker", "Śledzenie lokalizacji zatrzymane.")
    }

    /**
     * Wysyła dane lokalizacji użytkownika (przykładowo do bazy danych lub logów).
     * Można tutaj dodać logikę do przesyłania danych do bazy lub innego systemu.
     *
     * @param latitude Szerokość geograficzna lokalizacji.
     * @param longitude Długość geograficzna lokalizacji.
     */
    private fun sendLocationToDatabase(latitude: Double, longitude: Double) {
        // Przykładowy kod; tutaj mogłaby znajdować się logika do wysyłania danych do bazy
        Log.d("SendLocation", "Lokalizacja wysłana: Lat: $latitude, Lng: $longitude")
    }
}
