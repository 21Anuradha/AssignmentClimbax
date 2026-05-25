package com.example.assignmentclimbax.domain.model

data class CartItem(
    val productId: Int,
    val title: String,
    val price: Double,
    val thumbnail: String,
    val quantity: Int
) {
    val lineTotal: Double get() = price * quantity
}
