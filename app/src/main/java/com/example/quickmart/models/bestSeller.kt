package com.example.quickmart.models

data class bestSeller(
    val id : String?=null,
    val productType : String ?= null,
    val products : ArrayList<Product> ?= null
)
