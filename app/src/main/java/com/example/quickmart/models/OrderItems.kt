package com.example.quickmart.models

data class OrderItems(
    val orderId : String?= null,
    val itemDate : String?= null,
    val itemStatus : Int ?= null,
    val itemTitle : String?= null,
    val itemPrice : Int?= null
)
