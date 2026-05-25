package com.example.assignmentclimbax.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.assignmentclimbax.data.local.entity.CartEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {

    @Query("SELECT * FROM cart_items ORDER BY productId ASC")
    fun observeCartItems(): Flow<List<CartEntity>>

    @Query("SELECT * FROM cart_items ORDER BY productId ASC")
    suspend fun getCartItems(): List<CartEntity>

    @Query("SELECT quantity FROM cart_items WHERE productId = :productId LIMIT 1")
    suspend fun getQuantity(productId: Int): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: CartEntity): Long

    @Query("UPDATE cart_items SET quantity = :quantity WHERE productId = :productId")
    suspend fun updateQuantity(productId: Int, quantity: Int): Int

    @Query("DELETE FROM cart_items WHERE productId = :productId")
    suspend fun deleteByProductId(productId: Int): Int

    @Query("DELETE FROM cart_items")
    suspend fun clearAll(): Int
}
