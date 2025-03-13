package com.example.quickmart.models

import java.util.UUID

data class Product(
    var productRandomId: String? = null,
    var producttitle: String ?= null,
    var productQuantity: Int ? = null,
    var productUnit: String ?= null,
    var productPrice: Int ?= null,
    var productStock: Int ?= null,
    var productcategory: String ?= null,
    var producttype: String ?= null,
    var itemCount: Int ?= null,
    var adminUid: String ?= null,
    var productImageuris: ArrayList<String?> ?= null,
)
