package com.example.assignmentclimbax.domain.usecase

import com.example.assignmentclimbax.domain.model.CartItem
import com.example.assignmentclimbax.domain.model.Product
import com.example.assignmentclimbax.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow

class ObserveCartItemsUseCase(private val cartRepository: CartRepository) {
    operator fun invoke(): Flow<List<CartItem>> = cartRepository.observeCartItems()
}

class ObserveCartQuantitiesUseCase(private val cartRepository: CartRepository) {
    operator fun invoke(): Flow<Map<Int, Int>> = cartRepository.observeCartQuantities()
}

class AddToCartUseCase(private val cartRepository: CartRepository) {
    suspend operator fun invoke(product: Product) = cartRepository.addToCart(product)
}

class IncrementCartQuantityUseCase(private val cartRepository: CartRepository) {
    suspend operator fun invoke(productId: Int) = cartRepository.incrementQuantity(productId)
}

class DecrementCartQuantityUseCase(private val cartRepository: CartRepository) {
    suspend operator fun invoke(productId: Int) = cartRepository.decrementQuantity(productId)
}

class CheckoutUseCase(private val cartRepository: CartRepository) {
    suspend operator fun invoke(userId: Int): Result<Unit> = cartRepository.checkout(userId)
}
