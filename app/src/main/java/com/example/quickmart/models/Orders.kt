package com.example.quickmart.models

import com.example.quickmart.roomDb.cartProducts

data class Orders(
    val orderId : String,
    val orderList : List<cartProducts>? = null,
    val userAddress : String? = null,
    val orderStatus : Int? = 0,
    val orderDate : String?= null,
    val orderUserId : String?= null
)
