package com.example.quickmart.fragments

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.quickmart.R
import com.example.quickmart.databinding.FragmentSignInBinding
import com.example.quickmart.utils.utils


class SignInFragment : Fragment() {
   private lateinit var binding: FragmentSignInBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignInBinding.inflate(layoutInflater)

        setStatusBarColor()
        getUserNumber()
        onContinueClick()

        return binding.root
    }

    private fun onContinueClick() {
        binding.loginBtn.setOnClickListener {
            val number = binding.userMobileNumber.text.toString()

            Log.d("signin" , "number - ${number}")

            if (number.isEmpty() || number.length != 10) {
                Log.d("signin" , "if call hua")
                utils.showToast(requireContext(), "Please Enter Valid Number")
            } else {
                Log.d("signin" , "else call hua")
                val bundle = Bundle()
                bundle.putString("number", number)
                Log.d("signin" , "bundle number - ${number}")
                findNavController().navigate(R.id.action_signInFragment_to_otpFragment, bundle)
            }
        }
    }
    private fun getUserNumber() {
        binding.userMobileNumber.addTextChangedListener(object  : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Log.d("signin" , "before : ${binding.userMobileNumber}")
            }

            override fun onTextChanged(number: CharSequence?, start: Int, before: Int, count: Int) {
              val len = number?.length
                Log.d("signin" , "len : ${len}")
                if(len == 10){
                    binding.loginBtn.setBackgroundColor(ContextCompat.getColor(requireContext(),
                        R.color.green))
                    Log.d("signin" , "if mme aaya 2")
                }
                else{
                    binding.loginBtn.setBackgroundColor(ContextCompat.getColor(requireContext(),
                        R.color.gravish_blue
                    ))
                }
            }

            override fun afterTextChanged(s: Editable?) {
                Log.d("signin" , "after ")
            }

        })
    }


    private fun setStatusBarColor(){
        activity?.window?.apply {
            val statusBarColors = ContextCompat.getColor(requireContext() , R.color.yellow)
            statusBarColor = statusBarColors
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }

}