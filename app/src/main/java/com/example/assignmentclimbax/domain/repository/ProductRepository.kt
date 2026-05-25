package com.example.assignmentclimbax.domain.repository

import com.example.assignmentclimbax.domain.model.Product
import com.example.assignmentclimbax.domain.model.ProductPage

interface ProductRepository {
    suspend fun getProducts(limit: Int, skip: Int): Result<ProductPage>
    suspend fun searchProducts(query: String): Result<List<Product>>
}
