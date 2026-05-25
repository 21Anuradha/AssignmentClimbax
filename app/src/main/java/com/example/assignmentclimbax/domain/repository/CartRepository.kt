package com.example.assignmentclimbax.domain.repository

import com.example.assignmentclimbax.domain.model.CartItem
import com.example.assignmentclimbax.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    fun observeCartItems(): Flow<List<CartItem>>
    fun observeCartQuantities(): Flow<Map<Int, Int>>
    suspend fun addToCart(product: Product)
    suspend fun incrementQuantity(productId: Int)
    suspend fun decrementQuantity(productId: Int)
    suspend fun checkout(userId: Int): Result<Unit>
}
