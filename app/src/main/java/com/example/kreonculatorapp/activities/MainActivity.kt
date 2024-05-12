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
import com.example.kreonculatorapp.firestore.DataOperations
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class MainActivity : AppCompatActivity() {
    private lateinit var productEditText: EditText
    private lateinit var grammatureEditText: EditText
    private lateinit var addButton: Button
    private lateinit var productsList: ListView
    private lateinit var newProductButton: Button
    private lateinit var adapter: ArrayAdapter<String>
    private var ingredients: MutableList<String> = mutableListOf()

    val db = Firebase.firestore
    private val dbOperations = DataOperations(db)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        enableEdgeToEdge()
        initializeViews()
        setupListeners()
    }

    private fun initializeViews() {
        supportActionBar?.title = "KreonCulator"
        productEditText = findViewById(R.id.productEditText)
        grammatureEditText = findViewById(R.id.grammatureEditText)
        addButton = findViewById(R.id.addButton)
        productsList = findViewById(R.id.productsList)
        ingredients = ArrayList()
        newProductButton = findViewById(R.id.newMealButton)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, ingredients)
        productsList.adapter = adapter
    }

    private fun setupListeners() {
        addButton.setOnClickListener {
            val product = productEditText.text.toString()
            val grammature = grammatureEditText.text.toString()
            if (product.isNotEmpty() && grammature.isNotEmpty()) {
                val product = "$product - $grammature g"
                ingredients.add(product)
                refreshIngredientList()
            } else {
                Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show()
            }
        }

        newProductButton.setOnClickListener {
            val intent = Intent(this, CreateNewProduct::class.java)
            startActivity(intent)

            TODO()
            TODO("należy dodać listener do pola product, który przenosi do searchView")

        }
    }

    private fun refreshIngredientList() {
        adapter.notifyDataSetChanged()
    }
}
