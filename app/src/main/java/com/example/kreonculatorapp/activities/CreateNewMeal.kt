package com.example.kreonculatorapp.activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.kreonculatorapp.R
import com.google.firebase.firestore.FirebaseFirestore

class CreateNewMeal : AppCompatActivity() {
    private lateinit var newMealEditText: EditText
    private lateinit var addMealButton: Button
    private lateinit var fatEditText: EditText
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_new_meal)
        enableEdgeToEdge()
        initializeViews()
        setupListeners()
        db = FirebaseFirestore.getInstance()
    }

    private fun initializeViews() {
        supportActionBar?.title = "Create New Meal"
        newMealEditText = findViewById(R.id.newMealEditText)
        addMealButton = findViewById(R.id.addMealButton)
        fatEditText = findViewById(R.id.fatEditText)
    }

    private fun setupListeners() {
        addMealButton.setOnClickListener {
            val mealName = newMealEditText.text.toString()
            val fat = fatEditText.text.toString()
            if (mealName.isNotEmpty() && fat.isNotEmpty()) {
                addProductToFirestore(mealName, fat.toDouble())
            } else {
                Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addProductToFirestore(name: String, fat: Double) {
        val product = hashMapOf(
            "name" to name,
            "fat" to fat
        )

        db.collection("products")
            .add(product)
            .addOnSuccessListener {
                Toast.makeText(this, "Product added to Firestore!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error adding product to Firestore!", Toast.LENGTH_SHORT).show()
            }
    }

}
