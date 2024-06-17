package com.example.kreonculatorapp.activities.recyclerview

import android.app.Activity
import android.content.Intent
import android.os.Bundle
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

class ProductList : AppCompatActivity(), ProductAdapter.OnProductClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private lateinit var dbOperations: DataOperations

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_list)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        dbOperations = DataOperations(Firebase.firestore)

        GlobalScope.launch(Dispatchers.Main) {
            val products = dbOperations.getAllProducts()
            setupRecyclerView(products)
        }
    }

    private fun setupRecyclerView(products: List<Product>) {
        productAdapter = ProductAdapter(products, this@ProductList)
        recyclerView.adapter = productAdapter
    }

    override fun onProductClick(product: Product) {
        val intent = Intent()
        intent.putExtra("selected_product", product.name)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}
