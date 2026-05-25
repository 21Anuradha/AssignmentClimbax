package com.example.assignmentclimbax.domain.model

data class ProductPage(
    val products: List<Product>,
    val total: Int,
    val skip: Int,
    val limit: Int
) {
    val hasMore: Boolean get() = skip + products.size < total
}
