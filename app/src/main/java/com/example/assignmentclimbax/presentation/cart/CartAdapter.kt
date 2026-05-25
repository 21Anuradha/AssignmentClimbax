package com.example.assignmentclimbax.presentation.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.assignmentclimbax.databinding.ItemCartBinding
import com.example.assignmentclimbax.domain.model.CartItem
import java.util.Locale

class CartAdapter(
    private val onIncrement: (Int) -> Unit,
    private val onDecrement: (Int) -> Unit
) : ListAdapter<CartItem, CartAdapter.CartViewHolder>(CartDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CartViewHolder(
        private val binding: ItemCartBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CartItem) {
            binding.textCartTitle.text = item.title
            binding.textCartPrice.text = String.format(Locale.US, "$%.2f each", item.price)
            binding.textCartLineTotal.text = String.format(Locale.US, "$%.2f", item.lineTotal)
            binding.textCartQuantity.text = item.quantity.toString()
            binding.imageCartThumbnail.load(item.thumbnail) { crossfade(true) }
            binding.buttonCartIncrement.setOnClickListener { onIncrement(item.productId) }
            binding.buttonCartDecrement.setOnClickListener { onDecrement(item.productId) }
        }
    }

    private class CartDiffCallback : DiffUtil.ItemCallback<CartItem>() {
        override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem) =
            oldItem.productId == newItem.productId

        override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem) = oldItem == newItem
    }
}
