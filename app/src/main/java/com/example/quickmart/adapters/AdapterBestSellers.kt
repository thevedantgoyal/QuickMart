package com.example.quickmart.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.quickmart.databinding.ItemViewBestsellersBinding
import com.example.quickmart.models.bestSeller

class AdapterBestSellers(val onSeeAllClicked: (bestSeller) -> Unit) : RecyclerView.Adapter<AdapterBestSellers.BestSellerViewHolder>() {
    class BestSellerViewHolder(val binding : ItemViewBestsellersBinding) : ViewHolder(binding.root)

    val diffUtil = object : DiffUtil.ItemCallback<bestSeller>(){
        override fun areItemsTheSame(oldItem: bestSeller, newItem: bestSeller): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: bestSeller, newItem: bestSeller): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this , diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestSellerViewHolder {
        return BestSellerViewHolder(ItemViewBestsellersBinding.inflate(LayoutInflater.from(parent.context) , parent , false))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: BestSellerViewHolder, position: Int) {
        val productType = differ.currentList[position]
        holder.binding.apply {
            tvCategoryTitle.text = productType.productType
            tvCategoryCount.text = productType.products?.size.toString() + " products"

            val listOfIv = listOf(ivProduct1 , ivProduct2 , ivProduct3)

            val minimumSize = minOf(listOfIv.size , productType.products?.size!!)

            for( i in 0 until minimumSize){
                listOfIv[i].visibility = View.VISIBLE
                Glide.with(holder.itemView).load(productType.products[i].productImageuris?.get(0)).into(listOfIv[i])
            }

            if(productType.products.size!! > 3){
                tvProductCount.text = "+" + (productType.products.size!! - 3).toString()
            }

        }

        holder.itemView.setOnClickListener {
            onSeeAllClicked(productType)
        }

    }

}