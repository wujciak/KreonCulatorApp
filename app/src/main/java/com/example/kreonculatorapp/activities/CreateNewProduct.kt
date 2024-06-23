package com.example.kreonculatorapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.kreonculatorapp.R
import com.example.kreonculatorapp.firestore.DataOperations
import com.example.kreonculatorapp.firestore.Product
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CreateNewProduct : AppCompatActivity() {
    private lateinit var newProductEditText: EditText
    private lateinit var fatEditText: EditText
    private lateinit var addProductButton: Button

    val db = Firebase.firestore
    private val dbOperations = DataOperations(db)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_product)
        enableEdgeToEdge()
        initializeViews()
        setupListeners()
    }

    private fun initializeViews() {
        supportActionBar?.title = "Create New Product"
        newProductEditText = findViewById(R.id.newProductEditText)
        fatEditText = findViewById(R.id.fatEditText)
        addProductButton = findViewById(R.id.addProductButton)
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun setupListeners() {
        addProductButton.setOnClickListener {
            val name = newProductEditText.text.toString()
            val fat = fatEditText.text.toString().toDoubleOrNull()
            if (name.isNotEmpty() && fat != null) {
                val product = Product(name, fat)
                GlobalScope.launch(Dispatchers.Main) {
                    dbOperations.addProduct(name, product)
                }
                Toast.makeText(this, "Product added successfully!", Toast.LENGTH_SHORT).show()
                val resultIntent = Intent()
                setResult(RESULT_OK, resultIntent)
                finish()
            } else {
                Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
