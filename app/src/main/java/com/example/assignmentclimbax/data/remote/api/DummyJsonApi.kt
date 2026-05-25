package com.example.assignmentclimbax.data.remote.api

import com.example.assignmentclimbax.data.remote.dto.AddCartRequestDto
import com.example.assignmentclimbax.data.remote.dto.AddCartResponseDto
import com.example.assignmentclimbax.data.remote.dto.LoginRequestDto
import com.example.assignmentclimbax.data.remote.dto.LoginResponseDto
import com.example.assignmentclimbax.data.remote.dto.ProductsResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface DummyJsonApi {

    @GET("products")
    suspend fun getProducts(
        @Query("limit") limit: Int = 30,
        @Query("skip") skip: Int = 0
    ): ProductsResponseDto

    @GET("products/search")
    suspend fun searchProducts(@Query("q") query: String): ProductsResponseDto

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequestDto): LoginResponseDto

    @POST("carts/add")
    suspend fun addCart(@Body request: AddCartRequestDto): AddCartResponseDto
}
