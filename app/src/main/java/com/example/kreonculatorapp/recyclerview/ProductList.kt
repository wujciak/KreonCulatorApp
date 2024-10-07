package com.example.kreonculatorapp.recyclerview

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kreonculatorapp.R
import com.example.kreonculatorapp.firestore.Product
import com.example.kreonculatorapp.firestore.toProduct
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ProductList : AppCompatActivity(), OnProductClickListener {
    val db = Firebase.firestore
    val productList: MutableList<Product> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_list)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        db.collection("products").get().addOnCompleteListener(OnCompleteListener { task ->
            if (task.isSuccessful) {
                for (document in task.result) {
                    val product = document.toProduct()
                    if (product != null) {
                        productList.add(product)
                    }
                }
                val adapter = ProductAdapter(productList, this)
                recyclerView.adapter = adapter
            } else {
                Log.d("ProductList", "Error getting documents: ", task.exception)
            }
        })
    }

    override fun onProductClick(product: Product) {
        val resultIntent = Intent()
        resultIntent.putExtra("selected_product", product.name)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }
}
