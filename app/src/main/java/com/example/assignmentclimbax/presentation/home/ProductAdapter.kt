package com.example.assignmentclimbax.presentation.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.assignmentclimbax.databinding.ItemProductBinding
import com.example.assignmentclimbax.domain.model.Product
import java.util.Locale

class ProductAdapter(
    private val onAddToCart: (Product) -> Unit,
    private val onIncrement: (Int) -> Unit,
    private val onDecrement: (Int) -> Unit
) : ListAdapter<Product, ProductAdapter.ProductViewHolder>(ProductDiffCallback()) {

    private var cartQuantities: Map<Int, Int> = emptyMap()

    fun updateCartQuantities(quantities: Map<Int, Int>) {
        cartQuantities = quantities
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position), cartQuantities[getItem(position).id] ?: 0)
    }

    inner class ProductViewHolder(
        private val binding: ItemProductBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product, quantity: Int) {
            binding.textProductId.text = "ID: ${product.id}"
            binding.textProductTitle.text = product.title
            binding.textProductPrice.text = String.format(Locale.US, "$%.2f", product.price)
            binding.imageThumbnail.load(product.thumbnail) {
                crossfade(true)
            }

            val inCart = quantity > 0
            binding.buttonAddToCart.visibility = if (inCart) View.GONE else View.VISIBLE
            binding.layoutQuantity.visibility = if (inCart) View.VISIBLE else View.GONE
            binding.textQuantity.text = quantity.toString()

            binding.buttonAddToCart.setOnClickListener { onAddToCart(product) }
            binding.buttonIncrement.setOnClickListener { onIncrement(product.id) }
            binding.buttonDecrement.setOnClickListener { onDecrement(product.id) }
        }
    }

    private class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Product, newItem: Product) = oldItem == newItem
    }
}
