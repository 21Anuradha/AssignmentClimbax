package com.example.assignmentclimbax.data.repository

import com.example.assignmentclimbax.data.local.dao.CartDao
import com.example.assignmentclimbax.data.local.entity.CartEntity
import com.example.assignmentclimbax.data.local.entity.toDomain
import com.example.assignmentclimbax.data.remote.NetworkErrorMapper
import com.example.assignmentclimbax.data.remote.api.DummyJsonApi
import com.example.assignmentclimbax.data.remote.dto.AddCartRequest
import com.example.assignmentclimbax.data.remote.dto.CartProduct
import com.example.assignmentclimbax.domain.model.CartItem
import com.example.assignmentclimbax.domain.model.Product
import com.example.assignmentclimbax.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CartRepositoryImpl(
    private val cartDao: CartDao,
    private val api: DummyJsonApi
) : CartRepository {

    override fun observeCartItems(): Flow<List<CartItem>> =
        cartDao.observeCartItems().map { entities -> entities.map { it.toDomain() } }

    override fun observeCartQuantities(): Flow<Map<Int, Int>> =
        cartDao.observeCartItems().map { items ->
            items.associate { it.productId to it.quantity }
        }

    override suspend fun addToCart(product: Product) {
        val existingQty = cartDao.getQuantity(product.id)
        if (existingQty == null) {
            cartDao.upsert(
                CartEntity(
                    productId = product.id,
                    title = product.title,
                    price = product.price,
                    thumbnail = product.thumbnail,
                    quantity = 1
                )
            )
        } else {
            cartDao.updateQuantity(product.id, existingQty + 1)
        }
    }

    override suspend fun incrementQuantity(productId: Int) {
        val qty = cartDao.getQuantity(productId) ?: return
        cartDao.updateQuantity(productId, qty + 1)
    }

    override suspend fun decrementQuantity(productId: Int) {
        val qty = cartDao.getQuantity(productId) ?: return
        if (qty <= 1) {
            cartDao.deleteByProductId(productId)
        } else {
            cartDao.updateQuantity(productId, qty - 1)
        }
    }

    override suspend fun checkout(userId: Int): Result<Unit> {
        val items = cartDao.getCartItems()
        if (items.isEmpty()) {
            return Result.failure(Exception("Cart is empty. Add items before checkout."))
        }
        return try {
            api.addCart(
                AddCartRequest(
                    userId = userId,
                    products = items.map { CartProduct(id = it.productId, quantity = it.quantity) }
                )
            )
            cartDao.clearAll()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(NetworkErrorMapper.map(e), e))
        }
    }
}
