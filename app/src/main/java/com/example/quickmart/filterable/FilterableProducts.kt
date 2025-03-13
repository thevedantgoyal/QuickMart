package com.example.adminquickmart.filterable

import com.example.quickmart.adapters.ProductAdapter
import com.example.quickmart.models.Product
import java.util.Locale

class FilterableProducts(
    val adapter : ProductAdapter,
    val filter  : ArrayList<Product>
) : android.widget.Filter(){
    override fun performFiltering(constraint: CharSequence?): FilterResults {
        val results = FilterResults()

        if(!constraint.isNullOrEmpty()){
            val filteresList = ArrayList<Product>()
            val query = constraint.toString().trim().uppercase(Locale.getDefault()).split(" ")

            for(products in filter){
                if(query.any(){it
                        products.producttitle?.uppercase(Locale.getDefault())?.contains(it) == true ||
                                products.productcategory?.uppercase(Locale.getDefault())?.contains(it) == true ||
                                products.productPrice?.toString()?.uppercase(Locale.getDefault())?.contains(it) == true ||
                                products.producttype?.uppercase(Locale.getDefault())?.contains(it) == true
                    }){
                    filteresList.add(products)
                }
            }
            results.values = filteresList
            results.count = filteresList.size
        }
        else{
            results.values = filter
            results.count = filter.size
        }
        return results
    }
    override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
        adapter.differ.submitList(results?.values as ArrayList<Product>)
    }
}