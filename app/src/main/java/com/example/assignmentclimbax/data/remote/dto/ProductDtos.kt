package com.example.assignmentclimbax.data.remote.dto

import com.example.assignmentclimbax.domain.model.Product
import com.google.gson.annotations.SerializedName

data class ProductsResponse(
    val products: List<ProductDto>,
    val total: Int,
    val skip: Int,
    val limit: Int
)

data class ProductDto(
    val id: Int,
    val title: String,
    val thumbnail: String,
    val price: Double
)

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val id: Int,
    val email: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val image: String? = null,
    @SerializedName("accessToken") val accessToken: String? = null,
    @SerializedName("refreshToken") val refreshToken: String? = null,
    val token: String? = null
)

data class AddCartRequest(
    @SerializedName("userId") val userId: Int,
    val products: List<CartProduct>
)

data class CartProduct(
    val id: Int,
    val quantity: Int
)

data class AddCartResponse(
    val id: Int,
    val products: List<CartProduct>,
    val total: Double,
    val discountedTotal: Double,
    val userId: Int
)

fun ProductDto.toDomain(): Product = Product(
    id = id,
    title = title,
    thumbnail = thumbnail,
    price = price
)
