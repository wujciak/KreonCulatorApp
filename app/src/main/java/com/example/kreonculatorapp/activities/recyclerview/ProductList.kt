package com.example.kreonculatorapp.activities.recyclerview

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
    private lateinit var products: List<Product>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_list)

        recyclerView = findViewById(R.id.recyclerView)
        searchEditText = findViewById(R.id.searchEditText)

        recyclerView.layoutManager = LinearLayoutManager(this)
        dbOperations = DataOperations(Firebase.firestore)

        GlobalScope.launch(Dispatchers.Main) {
            products = dbOperations.getAllProducts()
            setupRecyclerView(products)
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val filteredList = products.filter {
                    it.name.contains(s.toString(), ignoreCase = true)
                }
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
