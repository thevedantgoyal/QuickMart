package com.example.quickmart.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController

import com.example.quickmart.R
import com.example.quickmart.adapters.AdapterOrders
import com.example.quickmart.databinding.FragmentOrdersBinding
import com.example.quickmart.models.OrderItems
import com.example.quickmart.utils.utils
import com.example.quickmart.viewModels.UserViewModel
import kotlinx.coroutines.launch

class OrdersFragment : Fragment() {
    private lateinit var binding : FragmentOrdersBinding
    private val viewModel : UserViewModel by viewModels()
    private lateinit var adapter : AdapterOrders
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrdersBinding.inflate(layoutInflater)

        getAllOrders()
        onBackBtnClicked()
        return binding.root
    }

    private fun getAllOrders() {
        binding.shimmerContainer.visibility = View.VISIBLE
        lifecycleScope.launch {
            viewModel.getAllProductsForStautus().collect{orderList->

                if(orderList.isNotEmpty()){
                    val orderedList = ArrayList<OrderItems>()
                    for(orders in orderList){

                        val title = StringBuilder()

                        var totalPrice = 0

                        for(products in orders.orderList!! ){
                            val price = products.productPrice?.substring(1)?.toInt()
                            val itemCount = products.productCount!!
                            totalPrice += (price?.times(itemCount)!!)

                            title.append("${products.productcategory}, ")
                        }

                        val orderedItems = OrderItems(
                            orderId = orders.orderId,
                            itemDate = orders.orderDate,
                            itemStatus = orders.orderStatus,
                            itemTitle = title.toString(),
                            itemPrice = totalPrice
                        )
                        orderedList.add(orderedItems)

                    }
                    adapter = AdapterOrders(requireContext() , ::onOrderItemViewClicked)
                    binding.rvOrders.adapter = adapter
                    adapter.differ.submitList(orderedList)
                    binding.shimmerContainer.visibility = View.GONE

                    Log.d("orders" , "adapter ke baad if ke andar")
                }
                else{
                    utils.showToast(requireContext() , "app crash order list is empty ")
                    Log.d("orders" , "adapter ke baad else ke andar")
                }
            }
        }

    }

    fun onOrderItemViewClicked(orderedItems: OrderItems){
        val bundle = Bundle()
        bundle.putInt("status" , orderedItems.itemStatus!!)
        bundle.putString("orderedId" , orderedItems.orderId)

        findNavController().navigate(R.id.action_ordersFragment_to_orderDetailFragment , bundle)
    }

    private fun onBackBtnClicked() {
        binding.orderBackBtn.setOnClickListener {
            findNavController().navigate(R.id.action_ordersFragment_to_profileFragment)
        }
    }
}