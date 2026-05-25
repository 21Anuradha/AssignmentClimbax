package com.example.assignmentclimbax.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.assignmentclimbax.domain.model.CartItem

@Entity(tableName = "cart_items")
data class CartEntity(
    @PrimaryKey val productId: Int,
    val title: String,
    val price: Double,
    val thumbnail: String,
    val quantity: Int
)

fun CartEntity.toDomain(): CartItem = CartItem(
    productId = productId,
    title = title,
    price = price,
    thumbnail = thumbnail,
    quantity = quantity
)

fun CartItem.toEntity(): CartEntity = CartEntity(
    productId = productId,
    title = title,
    price = price,
    thumbnail = thumbnail,
    quantity = quantity
)
