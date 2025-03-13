package com.example.quickmart.activity

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.quickmart.CartListener
import com.example.quickmart.R
import com.example.quickmart.adapters.AdapterCartProducts
import com.example.quickmart.databinding.ActivityOrderPlacedBinding
import com.example.quickmart.databinding.ItemProductDetailsBinding
import com.example.quickmart.databinding.UserAddressBinding
import com.example.quickmart.models.Orders
import com.example.quickmart.models.Product
import com.example.quickmart.objects.Constants
import com.example.quickmart.roomDb.cartProducts
import com.example.quickmart.utils.utils
import com.example.quickmart.viewModels.UserViewModel
import com.google.common.base.Charsets
import com.phonepe.intent.sdk.api.B2BPGRequest
import com.phonepe.intent.sdk.api.B2BPGRequestBuilder
import com.phonepe.intent.sdk.api.PhonePe
import com.phonepe.intent.sdk.api.PhonePeInitException
import com.phonepe.intent.sdk.api.models.PhonePeEnvironment
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.nio.charset.Charset
import java.security.MessageDigest


class OrderPlacedActivity : AppCompatActivity() , PaymentResultWithDataListener {
    private lateinit var binding : ActivityOrderPlacedBinding
    private val viewModel : UserViewModel by viewModels()
    private lateinit var adapterCartProducts : AdapterCartProducts
    private lateinit var b2BPGRequest : B2BPGRequest
    private var cartListener : CartListener?= null


    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityOrderPlacedBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        setStatusBarColor()
        backToUserMain()
        getAllCartProducts()
        initializeRazorpay()
        onPlacedOrderClicked()




        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initializeRazorpay() {
        Checkout.preload(applicationContext)
        val co = Checkout()
        co.setKeyID("rzp_test_OzQRNjUAgQsM0P")
    }


//    private fun initializePhonepay() {
//
//        val data = JSONObject()
//        PhonePe.init(this,PhonePeEnvironment.SANDBOX, Constants.MERCHANTID, "")
//
//        data.put("merchantId" , Constants.MERCHANTID)
//        data.put( "merchantTransactionId" , Constants.merchantTransactionId)
//        data.put("amount" , 200)
////        data.put("merchantUserId" , System.currentTimeMillis().toString())
//        data.put("mobileNumber" , "7007593142")
//        data.put("callbackUrl" , "https://webhook.site/callback-url")
//        // CHANGE HERE
//
//
//        val paymentInstruction = JSONObject()
//
//        paymentInstruction.put("type" ,  "UPI_INTENT")
//        paymentInstruction.put("targetApp" , "com.phonepe.simulator")
//
//        data.put("paymentInstruction " , paymentInstruction)
//
//        val deviceContext = JSONObject()
//            deviceContext.put("deviceOS" ,"ANDROID")
//            data.put("deviceContext" , deviceContext)
//
//        val payloadBase64 = Base64.encodeToString(
//            data.toString().toByteArray(Charset.defaultCharset()) , Base64.NO_WRAP
//        )
//
//        val checkSum = sha256(payloadBase64 + Constants.API_ENDPOINT + Constants.SALT_KEY ) + "###1";
//
//         b2BPGRequest = B2BPGRequestBuilder()
//            .setData(payloadBase64)
//            .setChecksum(checkSum)
//            .setUrl(Constants.API_ENDPOINT)
//            .build()
//        }

//    private fun sha256(input : String) : String{
//        val bytes = input.toByteArray(Charsets.UTF_8)
//        val md = MessageDigest.getInstance("SHA-256")
//        val digest = md.digest(bytes)
//        return digest.fold(""){str , it-> str + "%02x".format(it)}  // CHANGE HERE
//    }


    // basically this function is used to check when user is placed order first time so we save his address in firebase
    // and then after second time same user placed the order we redirect to phonepay integration.
    private fun onPlacedOrderClicked() {
        binding.llPlaceOrderBtn.setOnClickListener {
             viewModel.getUserAdress().observe(this){status->
                 if(status == true){
//                     getPaymmentView()
                     initPayment()
                 }
                 else{
                     val userAddress = UserAddressBinding.inflate(LayoutInflater.from(this))
                     val alertDialog = AlertDialog.Builder(this)
                         .setView(userAddress.root)
                         .create()
                         alertDialog.show()

                     userAddress.btnAddAddress.setOnClickListener {
                         savedUserAddress(alertDialog , userAddress)
                     }

                 }
             }
         }
    }

    private fun initPayment() {

        val activity: Activity = this
        val co = Checkout()

        try {
            val options = JSONObject()
            options.put("name","Quick Mart")
            options.put("description","Food Deleivery")
            //You can omit the image option to fetch the image from the dashboard
            options.put("image","http://example.com/image/rzp.jpg")
            options.put("theme.color", "#3399cc");
            options.put("currency","INR");
//            options.put("order_id", "order_DBJOWzybf0sJbb");
            options.put("amount","50000")//pass amount in currency subunits

            val retryObj = JSONObject();
            retryObj.put("enabled", true);
            retryObj.put("max_count", 4);
            options.put("retry", retryObj);

            val prefill = JSONObject()
            prefill.put("email","thevedantgoyal@gmail.com")
            prefill.put("contact","7007593142")

            options.put("prefill",prefill)
            co.open(activity,options)
        }catch (e: Exception){
            Toast.makeText(activity,"Error in payment: "+ e.message, Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

//    val phonePayView = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
//        if(it.resultCode == RESULT_OK){
//            checkStatus()
//        }
//    }

//    private fun checkStatus() {
//        val xVerify = sha256("/pg/v1/status/${Constants.MERCHANTID}/${Constants.merchantTransactionId}${Constants.SALT_KEY}") + "###1"
//        val headers = mapOf(
//            "Content-Type" to "application/json",
//            "X-VERIFY" to xVerify,
//            "X-MERCHANT-ID" to Constants.MERCHANTID,
//
//        )
//        lifecycleScope.launch {
//            viewModel.checkPayment(headers)
//            viewModel.paymentStatus.collect{status->
//                if(status){
//                    utils.showToast(this@OrderPlacedActivity , "Payment Successful")
//
//                    // before we have to save order in firebase what we have ordered
//                    saveOrder()
//
//                    // we also delete the cart product after payment done  which is saved in the order cart
//                    viewModel.deleteCartProduct()
//                    // after we delete the cart the count also deleted
//                    viewModel.savingItemCartCount(0)
//                    // we hide the cart also after all process done
//                    cartListener?.hideCartLayouts()
//
//                    utils.hideDialog()
//                    val intent = Intent(this@OrderPlacedActivity , OrderPlacedActivity::class.java)
//                    startActivity(intent)
//                    finish()
//                }
//                else
//                {
//                    utils.showToast(this@OrderPlacedActivity , "Payment Failed")
//                }
//            }
//        }
//    }

//    private fun getPaymmentView() {
//        try {
//            PhonePe.getImplicitIntent(this , b2BPGRequest , "com.phonepe.simulator")
//                .let {
//                    Log.d("phonepe" , "try me gya")
//                    phonePayView.launch(it)
//                }
//        }
//        catch (e : PhonePeInitException){
//            Log.d("phonepe" , "catch  - ${e.message.toString()}")
//            utils.showToast(this , "e.message.toString()")
//        }
//
//    }

    private fun saveOrder() {
        viewModel.getAll().observe(this){cartProductList->
            if(cartProductList.isNotEmpty()){
                viewModel.getUserAdressFromFirebase {address->
                    val orders = Orders(
                        userAddress = address,
                        orderId = utils.randomId(),
                        orderList = cartProductList,
                        orderStatus = 0,
                        orderDate = utils.getCurrentDate(),
                        orderUserId = utils.getCurrentUserid()
                    )

                    viewModel.saveOrderedroducts(orders)
                }
                for(products in cartProductList){
                    val count = products.productCount
                    val stock = products.productStock?.minus(count!!)
                    if(stock != null){
                        viewModel.saveProductsAfterOrder(stock , products)
                    }

                }
            }
        }
    }



//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (requestCode == 1) {
//            /*This callback indicates only about completion of UI flow.
//                   Inform your server to make the transaction
//                   status call to get the status. Update your app with the
//                   success/failure status.*/
//
//            utils.showToast(this , "money")
//        }
//    }

    private fun savedUserAddress(alertDialog: AlertDialog, userAddress: UserAddressBinding) {
        utils.showDialog(this , "Processing...")
        val userName = userAddress.userNameTxt.text.toString()
        val userPincode = userAddress.pinCodeText.text.toString()
        val userPhoneNumber = userAddress.phoneNoTxt.text.toString()
        val userState = userAddress.stateTxt.text.toString()
        val userDistrict = userAddress.districtTxt.text.toString()
        val userAdress = userAddress.addressTxt.text.toString()

        val concatenatedAdress = "$userPincode , $userDistrict($userState)) , $userAdress , $userPhoneNumber"



        lifecycleScope.launch {
            viewModel.savedUserAdressInFirebase(concatenatedAdress)
            alertDialog.dismiss()
            viewModel.saveAddressStatus()
            alertDialog.dismiss()
        }


        utils.showToast(this , "Saved...")
            initPayment()
    }

    private fun backToUserMain() {
        binding.backBtn.setOnClickListener {
            val intent = Intent(this , UserMainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun getAllCartProducts() {
        viewModel.getAll().observe(this){cartProductList->
            adapterCartProducts = AdapterCartProducts()
            binding.rvOrderItems.adapter = adapterCartProducts
            adapterCartProducts.differ.submitList(cartProductList)

            var totalPrice = 0
            for(products in cartProductList){
                val price = products.productPrice?.substring(1)?.toInt()
                val itemCount = products.productCount!!
                totalPrice += (price?.times(itemCount)!!)
                binding.tvSubtotal.text = "₹" + "${totalPrice.toString()}"
            }

            if(totalPrice < 200){
                binding.tvDeliveryFree.text = "₹15"
                totalPrice += 15
            }

            binding.tvGrandTotal.text = "₹" + "${totalPrice.toString()}"
        }
    }

    private fun setStatusBarColor(){
        window?.apply {
            val statusBarColors = ContextCompat.getColor(this@OrderPlacedActivity , R.color.yellow)
            statusBarColor = statusBarColors
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this , UserMainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun onAddBtnClicked(product: Product, productBinding: ItemProductDetailsBinding){
        productBinding.tvProductEdit.visibility = View.GONE
        productBinding.llproductCount.visibility = View.VISIBLE

        // Step - 1 ( when we click on add btn we visible the count of products and updated it
        // and also pass this count to item cart to updated there because it shows in all the screen)
        var itemCount = productBinding.tvProductCount.text.toString().toInt()
        itemCount++

        productBinding.tvProductCount.text = itemCount.toString()

        cartListener?.showCartLayout(1)

        // Step - 2 ( to save this information on room Database and firebase  )
        product.itemCount = itemCount
        lifecycleScope.launch {
            cartListener?.savingItemCartCount(1)
            saveProductInRoom(product)
            viewModel.updateItemCount(product , itemCount)
        }
    }

    private fun onIncrementBtnClicked(product: Product, productBinding: ItemProductDetailsBinding){
        var itemCountIncre = productBinding.tvProductCount.text.toString().toInt()
        itemCountIncre++

        if(product.productStock!! + 1 > itemCountIncre){
            productBinding.tvProductCount.text = itemCountIncre.toString()

            cartListener?.showCartLayout(1)

            // step - 2

            product.itemCount = itemCountIncre
            lifecycleScope.launch {
                cartListener?.savingItemCartCount(1)
                saveProductInRoom(product)
                viewModel.updateItemCount(product , itemCountIncre)
            }
        }
        else{
            utils.showToast(this, "Product is Out Of Stock!")
        }


    }

    private fun onDecrementBtnClicked(product: Product, productBinding: ItemProductDetailsBinding){
        var itemCountDecre = productBinding.tvProductCount.text.toString().toInt()
        itemCountDecre--


        /* we use this before because at we decrement the only one item in the cart like one to zero
        * so this lines of code add the product with zero item count at the room database but after
        * else condition also hit else itemcount = 0 so as else condition hit the deleteitemcart funct is call and
        * delete the product from the room database  */


        product.itemCount = itemCountDecre
        lifecycleScope.launch {
            cartListener?.savingItemCartCount(-1)
            saveProductInRoom(product)
            viewModel.updateItemCount(product , itemCountDecre)
        }


        if(itemCountDecre > 0){
            productBinding.tvProductCount.text = itemCountDecre.toString()
        }
        else{
            lifecycleScope.launch { viewModel.deleteCartProduct(product.productRandomId!!) }
            Log.d("VV" , "vv = ${product.productRandomId!!}")
            productBinding.tvProductEdit.visibility = View.VISIBLE
            productBinding.llproductCount.visibility = View.GONE
            productBinding.tvProductCount.text = "0"
        }
        cartListener?.showCartLayout(-1)


        // step - 2

    }

    private fun saveProductInRoom(product: Product) {
        val cartProduct = cartProducts(
            productId = product.productRandomId.toString(),
            producttitle = product.producttitle,
            productQuantity = product.productQuantity.toString() + product.productUnit.toString(),
            productPrice = "₹" + "${product.productPrice}",
            productCount = product.itemCount,
            productStock = product.productStock,
            productImage = product.productImageuris?.get(0),
            productcategory = product.productcategory,
            adminUid = product.adminUid,
            productType = product.producttype
        )
        lifecycleScope.launch { viewModel.insertCartProduct(cartProduct) }
    }

    override fun onPaymentSuccess(p0: String?, p1: PaymentData?) {
        lifecycleScope.launch {
            utils.showToast(this@OrderPlacedActivity , "Payment Successful")
//
            // before we have to save order in firebase what we have ordered
            saveOrder()

            // we also delete the cart product after payment done  which is saved in the order cart
            viewModel.deleteCartProduct()
            // after we delete the cart the count also deleted
            viewModel.savingItemCartCount(0)
            // we hide the cart also after all process done
            cartListener?.hideCartLayouts()

            utils.hideDialog()
            val intent = Intent(this@OrderPlacedActivity , OrderPlacedActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onPaymentError(p0: Int, p1: String?, p2: PaymentData?) {
        utils.showToast(this@OrderPlacedActivity , "Payment Failed")
    }
}

