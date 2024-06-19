package com.example.kreonculatorapp.activities.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kreonculatorapp.R
import com.example.kreonculatorapp.firestore.Product

interface OnProductClickListener {
    fun onProductClick(product: Product)
}

class ProductViewModel {
    val buttonStates: MutableMap<String, Boolean> = mutableMapOf()
}

class ProductAdapter(
    private val productList: List<Product>,
    private val clickListener: OnProductClickListener
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {
    private val productViewModel = ProductViewModel()

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.productNameTextView)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                val product = productList[position]
                clickListener.onProductClick(product)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.product_item, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.productName.text = product.name
    }

    override fun getItemCount() = productList.size
}