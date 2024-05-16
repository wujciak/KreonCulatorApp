package com.example.kreonculatorapp.activities.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.example.kreonculatorapp.R
import com.example.kreonculatorapp.firestore.Product
import java.util.*

class ProductAdapter(private val originalProducts: MutableList<Product>) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {
    private var filteredProducts = ArrayList<Product>()

    init {
        filteredProducts.addAll(originalProducts)
    }

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.product_item, parent, false)
        return ProductViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val currentProduct = filteredProducts[position]
        holder.nameTextView.text = currentProduct.name
    }

    override fun getItemCount() = filteredProducts.size

    fun filter(products: List<Product>) {
        filteredProducts.clear()
        filteredProducts.addAll(products)
        notifyDataSetChanged()
    }
}
