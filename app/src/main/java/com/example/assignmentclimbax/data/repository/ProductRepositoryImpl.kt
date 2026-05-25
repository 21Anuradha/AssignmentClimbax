package com.example.assignmentclimbax.data.repository

import com.example.assignmentclimbax.data.remote.NetworkErrorMapper
import com.example.assignmentclimbax.data.remote.api.DummyJsonApi
import com.example.assignmentclimbax.data.remote.dto.toDomain
import com.example.assignmentclimbax.domain.model.Product
import com.example.assignmentclimbax.domain.model.ProductPage
import com.example.assignmentclimbax.domain.repository.ProductRepository

class ProductRepositoryImpl(
    private val api: DummyJsonApi
) : ProductRepository {

    override suspend fun getProducts(limit: Int, skip: Int): Result<ProductPage> = try {
        val response = api.getProducts(limit = limit, skip = skip)
        Result.success(
            ProductPage(
                products = response.products.map { it.toDomain() },
                total = response.total,
                skip = response.skip,
                limit = response.limit
            )
        )
    } catch (e: Exception) {
        Result.failure(Exception(NetworkErrorMapper.map(e), e))
    }

    override suspend fun searchProducts(query: String): Result<List<Product>> = try {
        val products = if (query.isBlank()) {
            api.getProducts(limit = 30, skip = 0).products
        } else {
            api.searchProducts(query).products
        }
        Result.success(products.map { it.toDomain() })
    } catch (e: Exception) {
        Result.failure(Exception(NetworkErrorMapper.map(e), e))
    }
}
