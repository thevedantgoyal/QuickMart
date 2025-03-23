package com.example.quickmart

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quickmart.adapters.AdapterCartProducts
import com.example.quickmart.databinding.FragmentOrderDetailBinding
import com.example.quickmart.utils.utils
import com.example.quickmart.viewModels.UserViewModel
import kotlinx.coroutines.launch


class orderDetailFragment : Fragment() {
    private lateinit var binding : FragmentOrderDetailBinding
    private lateinit var adapter : AdapterCartProducts
    private  val viewModel : UserViewModel by viewModels()
    private  var status = 0
    private var orderId = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderDetailBinding.inflate(layoutInflater)

         getValues()
        settingStatus()
        getOrderedProduct()
        onBackBtnClicked()
        return binding.root
    }

    private fun getValues() {
        val bundle = arguments
        status = bundle?.getInt("status")!!
        orderId = bundle.getString("orderedId").toString()
    }

    private fun onBackBtnClicked() {
        binding.backBtn.setOnClickListener {
            findNavController().navigate(R.id.action_orderDetailFragment_to_ordersFragment)
        }
    }

    private fun getOrderedProduct() {
//        lifecycleScope.launch {
//            viewModel.getOrderedProducts(orderId).collect{cartList->
//                adapter = AdapterCartProducts()
//                binding.rvOrderDetail.adapter = adapter
//                adapter.differ.submitList(cartList)
//            }
//        }
        // ✅ Set LayoutManager

        lifecycleScope.launch {
            Log.d("CartList", "order id : ${orderId}")
            viewModel.getOrderedProducts(orderId).collect { cartList ->
                adapter = AdapterCartProducts()
                binding.rvOrderDetail.adapter = adapter
                if (cartList.isNullOrEmpty()) {
                    utils.showToast(requireContext(), "No orders found") // ✅ Prevent crash
                    Log.d("CartList", "Order list is empty")
                } else {
                    adapter.differ.submitList(cartList) // ✅ Only update list
                    Log.d("CartList", "Updated order list: $cartList")
                }
            }
        }
    }

    private fun settingStatus() {
        when(status){
            0->{
                binding.iv1.backgroundTintList = ContextCompat.getColorStateList(requireContext() , R.color.lightBlue)
            }
            1->{
                binding.iv1.backgroundTintList = ContextCompat.getColorStateList(requireContext() , R.color.lightBlue)
                binding.iv2.backgroundTintList = ContextCompat.getColorStateList(requireContext() , R.color.lightBlue)
                binding.view1.backgroundTintList = ContextCompat.getColorStateList(requireContext() , R.color.lightBlue)
            }
            2->{
                binding.iv1.backgroundTintList = ContextCompat.getColorStateList(requireContext() , R.color.lightBlue)
                binding.iv2.backgroundTintList = ContextCompat.getColorStateList(requireContext() , R.color.lightBlue)
                binding.view1.backgroundTintList = ContextCompat.getColorStateList(requireContext() , R.color.lightBlue)
                binding.iv3.backgroundTintList = ContextCompat.getColorStateList(requireContext() , R.color.lightBlue)
                binding.view2.backgroundTintList = ContextCompat.getColorStateList(requireContext() , R.color.lightBlue)
            }
            3->{
                binding.iv1.backgroundTintList = ContextCompat.getColorStateList(requireContext() , R.color.lightBlue)
                binding.iv2.backgroundTintList = ContextCompat.getColorStateList(requireContext() , R.color.lightBlue)
                binding.view1.backgroundTintList = ContextCompat.getColorStateList(requireContext() , R.color.lightBlue)
                binding.iv3.backgroundTintList = ContextCompat.getColorStateList(requireContext() , R.color.lightBlue)
                binding.view2.backgroundTintList = ContextCompat.getColorStateList(requireContext() , R.color.lightBlue)
                binding.iv4.backgroundTintList = ContextCompat.getColorStateList(requireContext() , R.color.lightBlue)
                binding.view3.backgroundTintList = ContextCompat.getColorStateList(requireContext() , R.color.lightBlue)
            }
        }
    }




}