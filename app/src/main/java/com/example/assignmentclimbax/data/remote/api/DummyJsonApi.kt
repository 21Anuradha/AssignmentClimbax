package com.example.assignmentclimbax.data.remote.api

import com.example.assignmentclimbax.data.remote.dto.AddCartRequest
import com.example.assignmentclimbax.data.remote.dto.AddCartResponse
import com.example.assignmentclimbax.data.remote.dto.LoginRequest
import com.example.assignmentclimbax.data.remote.dto.LoginResponse
import com.example.assignmentclimbax.data.remote.dto.ProductsResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface DummyJsonApi {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @GET("products")
    suspend fun getProducts(
        @Query("limit") limit: Int = 30,
        @Query("skip") skip: Int = 0
    ): ProductsResponse

    @GET("products/search")
    suspend fun searchProducts(@Query("q") query: String): ProductsResponse

    @POST("carts/add")
    suspend fun addCart(@Body request: AddCartRequest): AddCartResponse
}
