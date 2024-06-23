package com.example.kreonculatorapp.activities

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.kreonculatorapp.R

class ObtainedDose : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_obtained_dose)

        val result = intent.getIntExtra("result", 0)
        val resultTextView = findViewById<TextView>(R.id.result)
        resultTextView.text = result.toString()
    }
}
