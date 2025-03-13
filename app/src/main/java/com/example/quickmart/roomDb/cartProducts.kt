package com.example.quickmart.roomDb

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "CartProducts")
data class cartProducts (

    @PrimaryKey
    var productId: String = "random", // cant apply nullablity check here
    var producttitle: String ?= null,
    var productQuantity: String ? = null,
    var productCount: Int ?= null,
    var productPrice: String ?= null,
    var productStock: Int ?= null,
    var productcategory: String ?= null,
    var productImage: String ?= null,
    var adminUid: String ?= null,
    var productType : String ?= null
)

