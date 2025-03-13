package com.example.quickmart.viewModels

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.quickmart.models.OrderItems
import com.example.quickmart.models.Orders
import com.example.quickmart.models.Product
import com.example.quickmart.models.bestSeller
import com.example.quickmart.objects.Constants
import com.example.quickmart.roomDb.cartProducts
import com.example.quickmart.roomDb.cartProductsDao
import com.example.quickmart.roomDb.cartProductsDatabase
import com.example.quickmart.utils.utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow

class UserViewModel(application: Application) : AndroidViewModel(application) {


    // INITIALIZATIONS
    private val sharedPreference : SharedPreferences = application.getSharedPreferences("MyPref" , MODE_PRIVATE)
    private val cartProductDao : cartProductsDao = cartProductsDatabase.getDatabaseInstance(application).cartProductDao()
    private val _paymentStatus = MutableStateFlow<Boolean>(false)
    val paymentStatus = _paymentStatus




    //ROOM DB
    suspend fun insertCartProduct(products : cartProducts){
        cartProductDao.insertCartProduct(products)
    }

    suspend fun updateCartProducts(products: cartProducts){
        cartProductDao.updateCartProduct(products)
    }

    suspend fun deleteCartProduct(productId: String){
        cartProductDao.deleteCartProduct(productId)
    }

     suspend fun deleteCartProduct(){
        cartProductDao.deleteCartProducts()
    }

    fun getAll() : LiveData<List<cartProducts>>{
        return cartProductDao.getAllCartProducts()
    }


    // FIREBASE CALL
    // we fetch all products from database
    fun fetchAllProducts() : Flow<List<Product>> = callbackFlow {
        val db = FirebaseDatabase.getInstance().getReference("Admins").child("AllProducts")

        val eventListerner = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val products = ArrayList<Product>()
                for (product in snapshot.children){
                    val prod = product.getValue(Product::class.java)
                    products.add(prod!!)
                }
                trySend(products)
//                trysend is used to send the list
            }
            override fun onCancelled(error: DatabaseError) {
            }
        }
        db.addValueEventListener(eventListerner)
        awaitClose{ db.removeEventListener(eventListerner)}
    }


    fun getAllProductsForStautus() : Flow<List<Orders>> = callbackFlow {
        val db = FirebaseDatabase.getInstance().getReference("Admins").child("Orders").orderByChild("orderStatus")

        val eventListener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val orderList = ArrayList<Orders>()
                for(orders in snapshot.children){
                    val order = orders.getValue(Orders::class.java)
                    if(order?.orderUserId == utils.getCurrentUserid()){
                        orderList.add(order!!)
                    }
                }
                trySend(orderList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("db" , "orders oncancelled after orders")
            }

        }
        db.addValueEventListener(eventListener)
        awaitClose{ db.removeEventListener(eventListener) }

    }

    // Changes here

    // because this function we fetch only category products from the database
    fun getCategoryProduct(category : String) : Flow<List<Product>> = callbackFlow{
      val db = FirebaseDatabase.getInstance().getReference("Admins").child("ProductCategory/${category}")
        val eventListener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val products = ArrayList<Product>()
                for (product in snapshot.children){
                    val pro = product.getValue(Product::class.java)
                    products.add(pro!!)
                }
                   trySend(products)
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }
        // we add this db into event listener after work done we remove from eventlistener
        db.addValueEventListener(eventListener)
        awaitClose{ db.removeEventListener(eventListener)}
    }


    fun getOrderedProducts(orderId : String) : Flow<List<cartProducts>> = callbackFlow{
        val db = FirebaseDatabase.getInstance().getReference("Admins").child("Orders").child("orderId")
        val eventListener = object  : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val order = snapshot.getValue(Orders::class.java)
                trySend(order?.orderList!!)

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        db.addValueEventListener(eventListener)
        awaitClose{db.removeEventListener(eventListener)}
    }

    fun updateItemCount(product: Product , itemCount : Int){
        FirebaseDatabase.getInstance().getReference("Admins").child("AllProducts/${product.productRandomId}").child("itemCount").setValue(itemCount)
        FirebaseDatabase.getInstance().getReference("Admins").child("ProductCategory/${product.productcategory}/${product.productRandomId}").child("itemCount").setValue(itemCount)
        FirebaseDatabase.getInstance().getReference("Admins").child("ProductType/${product.producttype}/${product.productRandomId}").child("itemCount").setValue(itemCount)

    }

    fun saveProductsAfterOrder(stock : Int , product : cartProducts){
        FirebaseDatabase.getInstance().getReference("Admins").child("AllProducts/${product.productId}").child("itemCount").setValue(0)
        FirebaseDatabase.getInstance().getReference("Admins").child("ProductCategory/${product.productcategory}/${product.productId}").child("itemCount").setValue(0)
//        FirebaseDatabase.getInstance().getReference("Admins").child("ProductType/${product.producttype}/${product.productId}").child("itemCount").setValue(0)


        FirebaseDatabase.getInstance().getReference("Admins").child("AllProducts/${product.productId}").child("productStock").setValue(stock)
        FirebaseDatabase.getInstance().getReference("Admins").child("ProductCategory/${product.productcategory}/${product.productId}").child("productStock").setValue(stock)
//        FirebaseDatabase.getInstance().getReference("Admins").child("ProductType/${product.producttype}/${product.productId}").child("productStock").setValue(stock)


    }

    fun savedUserAdressInFirebase(address : String){
            FirebaseDatabase.getInstance().getReference("AllUsers").child("Users").child(utils.getCurrentUserid()!! ).child("userAddress").setValue(address)
    }
    fun getUserAdressFromFirebase(callback : (String?) -> Unit){

       val db =  FirebaseDatabase.getInstance().getReference("AllUsers").child("Users").child(utils.getCurrentUserid()!! ).child("userAddress")
        db.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val address = snapshot.getValue(String::class.java)
                    callback(address)
                }
                else{
                    callback(null)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null)

            }

        })
    }

    fun getAdminPhoneNumber(callback: (String?) -> Unit){
        val db = FirebaseDatabase.getInstance().getReference("AllUsers").child("Users").child(utils.getCurrentUserid()!!).child("userPhoneNumber")
         db.addValueEventListener(object : ValueEventListener{
             override fun onDataChange(snapshot: DataSnapshot) {
                 if (snapshot.exists()){
                     val phoneNumber = snapshot.getValue(String::class.java)
                     callback(phoneNumber)
                 }
                 else{
                     callback(null)
                 }
             }

             override fun onCancelled(error: DatabaseError) {
                 callback(null)
             }

         })
    }

    fun saveOrderedroducts(orders : Orders){
        FirebaseDatabase.getInstance().getReference("Admins").child("Orders").child(orders.orderId).setValue(orders)
    }




    // SHARED PREFERENCES
    fun savingItemCartCount(itemCount : Int){
        sharedPreference.edit().putInt("itemCount" , itemCount).apply()
    }
    fun fetchTotalCartCount() : MutableLiveData<Int>{
        val totalItemCount = MutableLiveData<Int>()
        totalItemCount.value = sharedPreference.getInt("itemCount" , 0)
        return totalItemCount
    }

    // we get the value in true of false so we know that if user address is saved before it return true or
    // if the user address is not saved then it return false
    fun getUserAdress() : MutableLiveData<Boolean>{
        val status = MutableLiveData<Boolean>()
        status.value = sharedPreference.getBoolean("addressStatus" , false)
        return  status
    }

    fun saveAddressStatus(){
        sharedPreference.edit().putBoolean("addressStatus" , true).apply()
    }

    fun fetchProductType() : Flow<List<bestSeller>> = callbackFlow {
        val db = FirebaseDatabase.getInstance().getReference("Admins/ProductType")

        val eventListener = object  : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val productTypeList = ArrayList<bestSeller>()
                for(productType in snapshot.children){
                    val productTypeName = productType.key

                    val productList = ArrayList<Product>()

                    for(products in productType.children){
                        val product = products.getValue(Product::class.java)
                        productList.add(product!!)
                    }

                    val bestseller = bestSeller(
                        productType = productTypeName,
                        products = productList
                    )
                    productTypeList.add(bestseller)
                }

                trySend(productTypeList)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        db.addValueEventListener(eventListener)
        awaitClose{ db.removeEventListener(eventListener) }

    }


    fun saveAddressinFireBase(address: String){
        FirebaseDatabase.getInstance().getReference("AllUsers").child("Users").child(utils.getCurrentUserid()!! ).child("userAddress").setValue(address)
    }

    fun LogOutUser(){
        FirebaseAuth.getInstance().signOut()
    }

    // Retrofit



}
