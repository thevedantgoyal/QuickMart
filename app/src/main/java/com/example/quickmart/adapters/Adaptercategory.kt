package com.example.quickmart.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.quickmart.databinding.ItemViewProductCategoryBinding
import com.example.quickmart.models.Category

class Adaptercategory(val categoryList: ArrayList<Category>, val onCategoryClicked: (Category) -> Unit) : RecyclerView.Adapter<Adaptercategory.CategoryViewHolder>(){

    class CategoryViewHolder(val binding : ItemViewProductCategoryBinding):ViewHolder(binding.root)




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
       return CategoryViewHolder(ItemViewProductCategoryBinding.inflate(LayoutInflater.from(parent.context) , parent , false))
    }

    override fun getItemCount(): Int {
       return categoryList.size
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categoryList[position]
       holder.binding.apply {
           ivCategory.setImageResource(category.image)
           tvCategoryText.text = category.title
       }

        holder.itemView.setOnClickListener {
            onCategoryClicked(category)
        }
    }


}
