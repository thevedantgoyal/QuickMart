package com.example.quickmart.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.quickmart.databinding.ActivityUserMainBinding
import com.example.quickmart.CartListener
import com.example.quickmart.adapters.AdapterCartProducts
import com.example.quickmart.databinding.BshadeItemCartBinding
import com.example.quickmart.roomDb.cartProducts
import com.example.quickmart.viewModels.UserViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog

class UserMainActivity : AppCompatActivity() , CartListener {
    private lateinit var binding : ActivityUserMainBinding
    private val viewModel : UserViewModel by viewModels()
    private lateinit var cartList : List<cartProducts>
    private lateinit var adapterCartProducts : AdapterCartProducts

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getAllItemFromRB()
        getTotalItemCountCart()
        onCartClicked()
        onNextBtnClicked()
    }

    private fun onNextBtnClicked() {
        binding.llNextBtn.setOnClickListener{
            val intent = Intent(this , OrderPlacedActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun getAllItemFromRB(){
        viewModel.getAll().observe(this){
            cartList = it
        }
    }

    private fun onCartClicked() {
        binding.llItemCart.setOnClickListener{
            val bshadeItemCart= BshadeItemCartBinding.inflate(LayoutInflater.from(this))

            val bs = BottomSheetDialog(this)
            bs.setContentView(bshadeItemCart.root)

            binding.llNextBtn.setOnClickListener{
                val intent = Intent(this , OrderPlacedActivity::class.java)
                startActivity(intent)
                finish()
            }
            bshadeItemCart.tvCartCount.text = binding.tvCartCount.text
            bshadeItemCart.llNextBtn.setOnClickListener {
                val intent = Intent(this , OrderPlacedActivity::class.java)
                startActivity(intent)
                finish()
            }
            adapterCartProducts = AdapterCartProducts(

            )
            bshadeItemCart.rvProductItems.adapter = adapterCartProducts
            adapterCartProducts.differ.submitList(cartList)

            bs.show()


        }

    }

    private fun getTotalItemCountCart() {
        viewModel.fetchTotalCartCount().observe(this){

            if(it  > 0){
                binding.llCart.visibility = View.VISIBLE
                binding.tvCartCount.text = it.toString()
            }
            else{
                binding.llCart.visibility = View.GONE
            }
        }
    }

    override fun showCartLayout(itemCount : Int) {
        val previousCount = binding.tvCartCount.text.toString().toInt()
        val updatedCount = previousCount + itemCount


        if(updatedCount > 0){
            binding.llCart.visibility = View.VISIBLE
            binding.tvCartCount.text = updatedCount.toString()
        }
        else if(updatedCount == 0 || updatedCount < 0){
            binding.llCart.visibility = View.GONE
            binding.tvCartCount.text = "0"
        }
    }

    override fun savingItemCartCount(itemCount: Int) {
        viewModel.fetchTotalCartCount().observe(this){
            viewModel.savingItemCartCount(it + itemCount)
        }
    }

    override fun hideCartLayouts() {
        binding.llCart.visibility = View.GONE
        binding.tvCartCount.text = "0"
    }


}