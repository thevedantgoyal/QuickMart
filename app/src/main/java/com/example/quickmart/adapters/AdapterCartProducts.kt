package com.example.quickmart.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.quickmart.databinding.ItemProductDetailsBinding
import com.example.quickmart.databinding.ItemViewCartProductsBinding
import com.example.quickmart.models.Product
import com.example.quickmart.roomDb.cartProducts

class AdapterCartProducts() : RecyclerView.Adapter<AdapterCartProducts.CartProductViewHolder>() {
    class CartProductViewHolder(val binding: ItemViewCartProductsBinding) : ViewHolder(binding.root)

    val diffUtil = object : DiffUtil.ItemCallback<cartProducts>() {
        override fun areItemsTheSame(oldItem: cartProducts, newItem: cartProducts): Boolean {
            return oldItem.productId == newItem.productId
        }

        override fun areContentsTheSame(oldItem: cartProducts, newItem: cartProducts): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this , diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartProductViewHolder {
       return CartProductViewHolder(ItemViewCartProductsBinding.inflate(LayoutInflater.from(parent.context) , parent , false))
    }

    override fun getItemCount(): Int {
       return differ.currentList.size
    }

    override fun onBindViewHolder(holder: CartProductViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.binding.apply {
            Glide.with(holder.itemView).load(product.productImage).into(ivProductImage)
            tvProductTitle.text = product.producttitle
            tvProductQuantity.text = product.productQuantity
            tvProductPrice.text = product.productPrice
            tvProductCount.text = product.productCount.toString()
        }
    }
}