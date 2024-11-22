package com.example.kreonculatorapp

import android.os.AsyncTask
import android.util.Log
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class NearbyPlacesTask(private val mMap: GoogleMap) : AsyncTask<String, Void, String?>() {

    override fun doInBackground(vararg urls: String): String? {
        val urlString = urls[0]
        val connection = URL(urlString).openConnection() as HttpURLConnection
        return try {
            connection.inputStream.bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            connection.disconnect()
        }
    }

    override fun onPostExecute(result: String?) {
        if (result != null) {
            try {
                val jsonObject = JSONObject(result)
                val results = jsonObject.getJSONArray("results")

                for (i in 0 until results.length()) {
                    val place = results.getJSONObject(i)
                    val name = place.getString("name")
                    val location = place.getJSONObject("geometry").getJSONObject("location")
                    val lat = location.getDouble("lat")
                    val lng = location.getDouble("lng")

                    val placeLocation = LatLng(lat, lng)
                    mMap.addMarker(MarkerOptions().position(placeLocation).title(name))
                }
            } catch (e: Exception) {
                Log.e("NearbyPlacesTask", "Błąd parsowania JSON: ${e.message}")
            }
        }
    }
}
