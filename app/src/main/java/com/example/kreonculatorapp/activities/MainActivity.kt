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
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    private lateinit var mealEditText: EditText
    private lateinit var grammatureEditText: EditText
    private lateinit var addButton: Button
    private lateinit var calcButton: Button
    private lateinit var ingredientList: ListView
    private lateinit var newMealButton: Button
    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: ArrayAdapter<String>
    private var ingredients: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        enableEdgeToEdge()
        initializeViews()
        setupListeners()
        db = FirebaseFirestore.getInstance()
        retrieveProductsFromFirestore()
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

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, ingredients)
        ingredientList.adapter = adapter
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

    private fun retrieveProductsFromFirestore() {
        db.collection("products")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val productName = document.getString("name")
                    val fat = document.getDouble("fat")
                    if (productName != null && fat != null) {
                        ingredients.add("$productName - $fat g")
                    }
                }
                refreshIngredientList()
            }
    }

    private fun refreshIngredientList() {
        adapter.notifyDataSetChanged()
    }

}
