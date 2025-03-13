package com.example.quickmart.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.quickmart.R
import com.example.quickmart.databinding.ItemViewOrdersBinding
import com.example.quickmart.models.OrderItems
import kotlin.reflect.KFunction1


class AdapterOrders(val context: Context, val orderItemViewClicked: (OrderItems)-> Unit) : RecyclerView.Adapter<AdapterOrders.OrdersViewHolder>() {
    class OrdersViewHolder(val binding : ItemViewOrdersBinding) : ViewHolder(binding.root)


    val differUtil = object : DiffUtil.ItemCallback<OrderItems>(){
        override fun areItemsTheSame(oldItem: OrderItems, newItem: OrderItems): Boolean {
            return oldItem.orderId == newItem.orderId
        }

        override fun areContentsTheSame(oldItem: OrderItems, newItem: OrderItems): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this , differUtil)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersViewHolder {
        return OrdersViewHolder(ItemViewOrdersBinding.inflate(LayoutInflater.from(parent.context) , parent , false))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: OrdersViewHolder, position: Int) {
        val order = differ.currentList[position]
        holder.binding.apply {
            tvOrderDate.text = order.itemDate
            tvOrderTitles.text = order.itemTitle
            tvOrderPrice.text = "₹${order.itemPrice.toString()}"
            when(order.itemStatus){
                0->{
                    tvOrderStatus.text = "Ordered"
                    tvOrderStatus.backgroundTintList = ContextCompat.getColorStateList(context , R.color.yellow)
                }
                1->{
                    tvOrderStatus.text = "Received"
                    tvOrderStatus.backgroundTintList = ContextCompat.getColorStateList(context , R.color.blue)
                }
                2->{
                    tvOrderStatus.text = "Dispatched"
                    tvOrderStatus.backgroundTintList = ContextCompat.getColorStateList(context , R.color.green)
                }
                3->{
                    tvOrderStatus.text = "Delivered"
                    tvOrderStatus.backgroundTintList = ContextCompat.getColorStateList(context , R.color.red)
                }
            }
        }
        holder.itemView.setOnClickListener {
            orderItemViewClicked(order)
        }
    }
}