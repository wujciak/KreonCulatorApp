package com.example.kreonculatorapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.kreonculatorapp.R

class MainActivity : AppCompatActivity() {
    // musimy zrobić logowanie, zamiast dodawanie użytkownika
    // musimy zrobic jakis raport zwrotny dla lekarza
    // input texty, text view
    // register
    // z każdego elementu możemy zrobić coś klikalne

    private lateinit var mealEditText: EditText
    private lateinit var grammatureEditText: EditText
    private lateinit var addButton: Button
    private lateinit var calcButton: Button
    private lateinit var ingredientList: ListView
    private lateinit var ingredients: MutableList<String>
    private lateinit var newMealButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        enableEdgeToEdge()
        initializeViews()
        setupListeners()
    }

    private fun initializeViews() {
        supportActionBar?.title = "KreonCulator"
        mealEditText = findViewById(R.id.mealEditText)
        grammatureEditText = findViewById(R.id.grammatureEditText)
        addButton = findViewById(R.id.addButton)
        calcButton = findViewById(R.id.calcButton)
        ingredientList = findViewById(R.id.ingredientList)
        ingredients = ArrayList()
        newMealButton = findViewById(R.id.newMealButton)
    }

    private fun setupListeners() {
        addButton.setOnClickListener {
            val meal = mealEditText.text.toString()
            val grammature = grammatureEditText.text.toString()
            if (meal.isNotEmpty() && grammature.isNotEmpty()) {
                val ingredient = "$meal - $grammature g"
                ingredients.add(ingredient)
                refreshIngredientList()
            } else {
                Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show()
            }
        }

        calcButton.setOnClickListener {
            // Tu będzie obliczana dawka leku
        }

        newMealButton.setOnClickListener {
            val intent = Intent(this, CreateNewMeal::class.java)
            startActivity(intent)
        }
    }

    private fun refreshIngredientList() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, ingredients)
        ingredientList.adapter = adapter
    }
}
