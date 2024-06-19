package com.example.kreonculatorapp.activities.recyclerview

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kreonculatorapp.R
import com.example.kreonculatorapp.firestore.DataOperations
import com.example.kreonculatorapp.firestore.Product
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ProductList : AppCompatActivity(), OnProductClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private lateinit var dbOperations: DataOperations
    private lateinit var searchEditText: EditText
    private val products: MutableList<Product> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_list)

        recyclerView = findViewById(R.id.recyclerView)
        searchEditText = findViewById(R.id.searchEditText)
        recyclerView.layoutManager = LinearLayoutManager(this)
        dbOperations = DataOperations(Firebase.firestore)

        GlobalScope.launch(Dispatchers.Main) {
            val loadedProducts = dbOperations.getAllProducts()
            products.clear()
            products.addAll(loadedProducts)
            if (products.isNotEmpty()) {
                Log.d("ProductList", "Products loaded successfully: ${products.size}")
            } else {
                Log.d("ProductList", "No products found")
            }
            setupRecyclerView(products)
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val filteredList = products.filter {
                    it.name.contains(s.toString(), ignoreCase = true)
                }
                Log.d("ProductList", "Filtered products count: ${filteredList.size}")
                setupRecyclerView(filteredList)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupRecyclerView(products: List<Product>) {
        productAdapter = ProductAdapter(products, this)
        recyclerView.adapter = productAdapter
    }

    override fun onProductClick(product: Product) {
        val intent = Intent()
        intent.putExtra("selected_product", product.name)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}
