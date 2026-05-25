package com.example.assignmentclimbax.data.remote.dto

import com.example.assignmentclimbax.domain.model.Product
import com.google.gson.annotations.SerializedName

data class ProductsResponseDto(
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

data class LoginRequestDto(
    val username: String,
    val password: String
)

data class LoginResponseDto(
    val id: Int,
    val username: String,
    val email: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val gender: String? = null,
    val image: String? = null,
    @SerializedName("accessToken") val accessToken: String? = null,
    @SerializedName("refreshToken") val refreshToken: String? = null,
    val token: String? = null
) {
    /** DummyJSON now returns accessToken; older responses used token. */
    fun resolvedToken(): String = accessToken?.takeIf { it.isNotBlank() }
        ?: token?.takeIf { it.isNotBlank() }
        ?: ""
}

data class AddCartRequestDto(
    @SerializedName("userId") val userId: Int,
    val products: List<CartProductDto>
)

data class CartProductDto(
    val id: Int,
    val quantity: Int
)

data class AddCartResponseDto(
    val id: Int,
    val products: List<CartProductDto>,
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
