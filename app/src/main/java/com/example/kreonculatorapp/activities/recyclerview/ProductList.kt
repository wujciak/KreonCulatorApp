package com.example.kreonculatorapp.activities.recyclerview

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kreonculatorapp.R
import com.example.kreonculatorapp.firestore.Product
import com.google.firebase.firestore.FirebaseFirestore

class ProductList : ComponentActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var products: MutableList<Product>
    private lateinit var adapter: ProductAdapter
    private lateinit var searchEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_list)

        firestore = FirebaseFirestore.getInstance()

        products = mutableListOf()

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        adapter = ProductAdapter(products)
        recyclerView.adapter = adapter

        recyclerView.layoutManager = LinearLayoutManager(this)

        searchEditText = findViewById(R.id.searchEditText) // Inicjalizacja searchEditText

        fetchProducts()

        setupSearch()
    }

    private fun fetchProducts() {
        firestore.collection("products")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val name = document.getString("name")
                    val fat = document.getDouble("fat") ?: 0.0
                    val product = Product(name ?: "", fat)
                    products.add(product)
                }
                // Sortowanie produktów alfabetycznie po nazwie
                products.sortBy { it.name }
                // Odświeżenie listy po pobraniu wszystkich danych
                adapter.notifyDataSetChanged()
                // Obsługa pustej listy
                if (products.isEmpty()) {
                    // Tutaj możesz wyświetlić komunikat informujący o braku produktów
                    Log.d(TAG, "Lista produktów jest pusta.")
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Błąd podczas pobierania dokumentów: ", exception)
            }
    }

    private fun setupSearch() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterProducts(s.toString())
            }
        })
    }

    private fun filterProducts(query: String) {
        val filteredList = mutableListOf<Product>()
        for (product in products) {
            if (product.name.contains(query, true)) {
                filteredList.add(product)
            }
        }
        adapter.filter(filteredList)
    }

    companion object {
        private const val TAG = "com.example.kreonculatorapp.activities.recyclerview.ProductList"
    }
}
