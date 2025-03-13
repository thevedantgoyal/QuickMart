package com.example.quickmart.fragments

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.quickmart.CartListener
import com.example.quickmart.R
import com.example.quickmart.adapters.AdapterBestSellers
import com.example.quickmart.adapters.Adaptercategory
import com.example.quickmart.adapters.ProductAdapter
import com.example.quickmart.databinding.BottomsheetSeeallBinding
import com.example.quickmart.databinding.FragmentHomeBinding
import com.example.quickmart.databinding.ItemProductDetailsBinding
import com.example.quickmart.models.Category
import com.example.quickmart.models.Product
import com.example.quickmart.models.bestSeller
import com.example.quickmart.objects.Constants
import com.example.quickmart.roomDb.cartProducts
import com.example.quickmart.utils.utils
import com.example.quickmart.viewModels.UserViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
   private lateinit var binding : FragmentHomeBinding
   private lateinit var adapter : AdapterBestSellers
   private lateinit var adapterProduct : ProductAdapter
    private var cartListener : CartListener ?= null
    val viewModel : UserViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setStatusBarColor()
        setAllCategories()
        navigationToSearchFragment()
        onProfiileClicked()
        fetchBestSeller()
        get()
    }

    private fun fetchBestSeller() {
        binding.shimmerBestSeller.visibility = View.VISIBLE
        lifecycleScope.launch {
            viewModel.fetchProductType().collect{
                adapter  = AdapterBestSellers(::onSeeAllClicked)
                binding.rvBestSellers.adapter = adapter
                adapter.differ.submitList(it)
                binding.shimmerBestSeller.visibility = View.GONE
            }
        }
    }

    fun onSeeAllClicked(productType : bestSeller){
        val bsSeeAllBinding = BottomsheetSeeallBinding.inflate(LayoutInflater.from(requireContext()))
        val bs = BottomSheetDialog(requireContext())
            bs.setContentView(bsSeeAllBinding.root)

        adapterProduct = ProductAdapter(::onAddBtnClicked , ::onIncrementBtnClicked , ::onDecrementBtnClicked)
        bsSeeAllBinding.rvProducts.adapter = adapterProduct
        adapterProduct.differ.submitList(productType.products)

        bs.show()
    }


    private fun onProfiileClicked() {
        binding.ivProfile.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
        }
    }

    private fun navigationToSearchFragment() {
        binding.searchView.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }
    }

    private fun setAllCategories() {
        val categoryList = ArrayList<Category>()

        for (i in 0 until Constants.allProductsCategory.size){
            categoryList.add(Category(Constants.allProductsCategory[i], Constants.allProductsCategoryIcon[i]))
        }

        binding.rvCategory.adapter = Adaptercategory(categoryList , ::onCategoryItemClicked)
    }

     fun onCategoryItemClicked(category: Category){
         val bundle = Bundle()
         bundle.putString("category" , category.title)
         findNavController().navigate(R.id.action_homeFragment_to_categoryFragment , bundle)
     }

    private fun get(){
        viewModel.getAll().observe(viewLifecycleOwner){
            for (i in it){
                Log.d("vvv" , i.producttitle.toString())
                Log.d("vvv" , i.productCount.toString())

            }
        }
    }

    private fun setStatusBarColor(){
        activity?.window?.apply {
            val statusBarColors = ContextCompat.getColor(requireContext() , R.color.white)
            statusBarColor = statusBarColors
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
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