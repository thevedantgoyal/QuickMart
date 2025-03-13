package com.example.quickmart

// we use interface because we manage the visibility of the card item below to display all the time when the
// item added to the cart
interface CartListener {

    fun showCartLayout(itemCount : Int)

    fun savingItemCartCount(itemCount: Int)

    fun hideCartLayouts()

}