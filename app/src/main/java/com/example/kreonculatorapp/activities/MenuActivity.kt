package com.example.kreonculatorapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.kreonculatorapp.R

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        // Inicjalizacja przycisków
        val obtainButton = findViewById<Button>(R.id.obtainButton)
        val mapButton = findViewById<Button>(R.id.mapButton)

        // Ustawienie paddingu dla głównego widoku w oparciu o systemowe marginesy
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Ustawienie OnClickListener dla przycisku obtainButton - przejście do MainActivity
        obtainButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // Ustawienie OnClickListener dla przycisku mapButton - przejście do MapsActivity
        mapButton.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }
    }
}
