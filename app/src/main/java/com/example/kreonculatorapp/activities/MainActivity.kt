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
import com.example.kreonculatorapp.activities.recyclerview.ProductList
import com.example.kreonculatorapp.firestore.DataOperations
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var productEditText: EditText
    private lateinit var grammatureEditText: EditText
    private lateinit var addButton: Button
    private lateinit var calcButton: Button
    private lateinit var productsList: ListView
    private lateinit var newProductButton: Button
    private lateinit var adapter: ArrayAdapter<String>
    private var ingredients: MutableList<String> = mutableListOf()
    private val dbOperations = DataOperations(FirebaseFirestore.getInstance())

    companion object {
        private const val REQUEST_CODE_SELECT_PRODUCT = 1
        private const val REQUEST_CODE_CREATE_PRODUCT = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeViews()
        setupListeners()
        enableEdgeToEdge()
    }

    private fun initializeViews() {
        productEditText = findViewById(R.id.productEditText)
        calcButton = findViewById(R.id.calcButton)
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
                val ingredient = "$product - $grammature g"
                ingredients.add(ingredient)
                refreshIngredientList()
            } else {
                Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show()
            }
        }

        newProductButton.setOnClickListener {
            val intent = Intent(this, CreateNewProduct::class.java)
            startActivityForResult(intent, REQUEST_CODE_CREATE_PRODUCT)
        }

        productEditText.setOnClickListener {
            val intent = Intent(this, ProductList::class.java)
            startActivityForResult(intent, REQUEST_CODE_SELECT_PRODUCT)
        }

        calcButton.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                var totalFatSum = 0.0
                for (ingredient in ingredients) {
                    val parts = ingredient.split(" - ")
                    val product = parts[0]
                    val grammature = parts[1].replace(" g", "").toDoubleOrNull()

                    grammature?.let { gram ->
                        val productData = dbOperations.getProduct(product)
                        productData?.let { product ->
                            val fat = product.fat
                            val totalFat = (fat.toDouble() * gram) / 100.0
                            totalFatSum += totalFat
                        }
                    }
                }
                val result = kotlin.math.ceil(totalFatSum / 5).toInt()
                val intent = Intent(this@MainActivity, ObtainedDose::class.java)
                intent.putExtra("result", result)
                startActivity(intent)
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SELECT_PRODUCT && resultCode == RESULT_OK) {
            val selectedProduct = data?.getStringExtra("selected_product")
            selectedProduct?.let {
                productEditText.setText(it)
            }
        } else if (requestCode == REQUEST_CODE_CREATE_PRODUCT && resultCode == RESULT_OK) {
            Toast.makeText(this, "New product added successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun refreshIngredientList() {
        adapter.notifyDataSetChanged()
    }
}
