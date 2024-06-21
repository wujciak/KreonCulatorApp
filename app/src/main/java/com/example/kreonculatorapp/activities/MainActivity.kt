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

class MainActivity : AppCompatActivity() {
    private lateinit var productEditText: EditText
    private lateinit var grammatureEditText: EditText
    private lateinit var addButton: Button
    private lateinit var productsList: ListView
    private lateinit var newProductButton: Button
    private lateinit var adapter: ArrayAdapter<String>
    private var ingredients: MutableList<String> = mutableListOf()

    companion object {
        private const val REQUEST_CODE_SELECT_PRODUCT = 1
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
            startActivity(intent)
        }

        productEditText.setOnClickListener {
            val intent = Intent(this, ProductList::class.java)
            startActivityForResult(intent, REQUEST_CODE_SELECT_PRODUCT)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SELECT_PRODUCT && resultCode == RESULT_OK) {
            val selectedProduct = data?.getStringExtra("selected_product")
            selectedProduct?.let {
                productEditText.setText(it)
            }
        }
    }

    private fun refreshIngredientList() {
        adapter.notifyDataSetChanged()
    }
}
