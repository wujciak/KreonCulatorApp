package com.example.kreonculatorapp.activities.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kreonculatorapp.R
import com.example.kreonculatorapp.firestore.Product

class ProductAdapter(
    private val productList: List<Product>,
    private val clickListener: OnProductClickListener
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    interface OnProductClickListener {
        fun onProductClick(product: Product)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.product_item, parent, false)
        return ProductViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.productName.text = product.name
        holder.itemView.setOnClickListener {
            clickListener.onProductClick(product)
        }
    }

    override fun getItemCount() = productList.size

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.productNameTextView)
    }
}
