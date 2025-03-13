package com.example.quickmart.roomDb

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.quickmart.models.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface  cartProductsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCartProduct(product : cartProducts)

    @Update
    fun updateCartProduct(product: cartProducts)

    @Query("SELECT * FROM cartProducts")
    fun getAllCartProducts() : LiveData<List<cartProducts>>

    @Query("DELETE FROM CartProducts WHERE productId = :productId")
    suspend fun deleteCartProduct(productId : String)

    @Query("DELETE FROM CartProducts")
    suspend fun deleteCartProducts()
}