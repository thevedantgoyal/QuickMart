package com.example.quickmart.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.quickmart.CartListener
import com.example.quickmart.R
import com.example.quickmart.adapters.ProductAdapter
import com.example.quickmart.databinding.FragmentSearchBinding
import com.example.quickmart.databinding.ItemProductDetailsBinding
import com.example.quickmart.models.Product
import com.example.quickmart.roomDb.cartProducts
import com.example.quickmart.utils.utils
import com.example.quickmart.viewModels.UserViewModel
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {
    val viewModel : UserViewModel by viewModels()
    private lateinit var binding: FragmentSearchBinding
    private lateinit var adapterProduct : ProductAdapter
    private var cartListener : CartListener ?= null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       binding = FragmentSearchBinding.inflate(layoutInflater)

        getAlltheProducts()
        backtoHomeFragment()
        return binding.root
    }

    private fun backtoHomeFragment() {
        binding.backBtn.setOnClickListener {
            findNavController().navigate(R.id.action_searchFragment_to_homeFragment)
        }
    }

    private fun getAlltheProducts() {
        binding.shimmerContainer.visibility = View.VISIBLE
        lifecycleScope.launch {
            viewModel.fetchAllProducts().collect{it

                if(it.isEmpty()){
                    binding.rvProducts.visibility = View.GONE
                    binding.tvText.visibility = View.VISIBLE
                }
                else{
                    binding.rvProducts.visibility = View.VISIBLE
                    binding.tvText.visibility = View.GONE
                }
                adapterProduct = ProductAdapter(
                    ::onAddBtnClicked,
                    ::onIncrementBtnClicked,
                    ::onDecrementBtnClicked
                )
                binding.rvProducts.adapter = adapterProduct
                adapterProduct.differ.submitList(it)
                adapterProduct.originalList = it as ArrayList<Product>
                binding.shimmerContainer.visibility = View.GONE
            }
        }
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

    // these two function are implement for + , -  on item cart after click on the add btn
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
            utils.showToast(requireContext() , "Product is Out Of Stock!")
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

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is CartListener){
            cartListener = context
        }
        else{
            throw ClassCastException("Please Implement Cart Listener")
        }
    }
}