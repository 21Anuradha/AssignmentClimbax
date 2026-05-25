package com.example.assignmentclimbax.data.remote

import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

object NetworkErrorMapper {

    fun map(throwable: Throwable): String = when (throwable) {
        is UnknownHostException, is SocketTimeoutException ->
            "No internet connection. Please check your network and try again."
        is IOException ->
            "Network error. Please check your connection and try again."
        is HttpException -> mapHttp(throwable)
        else -> throwable.message?.takeIf { it.isNotBlank() } ?: "Something went wrong. Please try again."
    }

    private fun mapHttp(exception: HttpException): String = when (exception.code()) {
        401 -> "Unauthorized. Please login again with valid credentials."
        403 -> "Access denied. You are not allowed to perform this action."
        404 -> "Requested resource was not found."
        in 500..599 -> "Server error. Please try again later."
        else -> "Request failed (${exception.code()}). Please try again."
    }

    fun isUnauthorized(throwable: Throwable): Boolean =
        throwable is HttpException && throwable.code() == 401
}
