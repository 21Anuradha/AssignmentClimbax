package com.example.assignmentclimbax.domain.usecase

import com.example.assignmentclimbax.domain.model.Product
import com.example.assignmentclimbax.domain.repository.ProductRepository

class SearchProductsUseCase(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(query: String): Result<List<Product>> =
        productRepository.searchProducts(query)
}
