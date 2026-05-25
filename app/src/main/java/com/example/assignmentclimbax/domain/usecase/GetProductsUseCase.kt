package com.example.assignmentclimbax.domain.usecase

import com.example.assignmentclimbax.domain.model.ProductPage
import com.example.assignmentclimbax.domain.repository.ProductRepository

class GetProductsUseCase(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(limit: Int, skip: Int): Result<ProductPage> =
        productRepository.getProducts(limit, skip)
}
