package com.example.quickmart.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController

import com.example.quickmart.R
import com.example.quickmart.activity.UserMainActivity
import com.example.quickmart.databinding.FragmentOtpBinding
import com.example.quickmart.utils.utils
import com.example.quickmart.viewModels.AuthViewModel
import kotlinx.coroutines.launch


class OtpFragment : Fragment() {
    private lateinit var binding : FragmentOtpBinding
    private lateinit var userNumber : String
    private val viewModel : AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOtpBinding.inflate(layoutInflater)



        getuserName()
        customizingOtpNumber()
        sendPhoneOtp()
        onLogginBtnClicked()
        onBackBtnClicked()

        return binding.root
    }

    private fun onLogginBtnClicked() {
        binding.otpLoginBtn.setOnClickListener {
            utils.showDialog(requireContext() , "Signing You...")
            val editTexts = arrayOf(binding.otpEt1,binding.otpEt2,binding.otpEt3,binding.otpEt4,binding.otpEt5,binding.otpEt6)
            val otp = editTexts.joinToString("") {it.text.toString()}
            if(otp.length < editTexts.size){
                utils.showToast(requireContext() , "Please Enter Otp!")
            }
            else {
                editTexts.forEach { it.text?.clear() ; it.clearFocus()
                }
                verifyOtp(otp)
            }
        }
    }

    private fun verifyOtp(otp: String) {

        viewModel.signInWithPhoneAuthCredential(otp, userNumber )


        lifecycleScope.launch {
            viewModel.signedSuccessfully.collect {it
                if (it) {
                    Log.d("otp" , "lifestyle in ")
                    utils.hideDialog()
                    utils.showToast(requireContext(), "Logged In...")
                    val intent = Intent(requireActivity(), UserMainActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }
            }
        }
    }

    private fun sendPhoneOtp() {
        utils.showDialog(requireContext() , "Sending Otp...")
        viewModel.apply {
            sendOtp(userNumber , requireActivity())
            lifecycleScope.launch {
                otpSent.collect{otpSent->
                    if(otpSent == true){
                        utils.hideDialog()
                        utils.showToast(requireContext() , "Otp Sent")
                    }
                }
            }
        }
    }
    private fun onBackBtnClicked(){
        binding.tbOtp.setNavigationOnClickListener{
            findNavController().navigate(R.id.action_otpFragment_to_signInFragment)
        }
    }

    private fun customizingOtpNumber() {
        val editTexts = arrayOf(binding.otpEt1,binding.otpEt2,binding.otpEt3,binding.otpEt4,binding.otpEt5,binding.otpEt6)
        for (i in editTexts.indices){
            editTexts[i].addTextChangedListener(object : TextWatcher{
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if(editTexts.size != 6){
                        binding.otpLoginBtn.setBackgroundColor(ContextCompat.getColor(requireContext() ,R.color.blue ))
                    }
                    else
                    {
                        binding.otpLoginBtn.setBackgroundColor(ContextCompat.getColor(requireContext() ,R.color.gravish_blue ))
                    }

                }
                override fun afterTextChanged(s: Editable?) {
                    if(s?.length == 1){
                        if(i < editTexts.size - 1 ){
                            editTexts[i+1].requestFocus()
                        }
                    }else if (s?.length==0){
                        if( i > 0){
                            editTexts[i-1].requestFocus()
                        }
                    }
                }

            })
        }
    }
    private fun getuserName() {

        val bundle = arguments
        userNumber = bundle?.getString("number").toString()


        binding.otpNumber.text = userNumber

    }


}