package com.example.quickmart.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.quickmart.R
import com.example.quickmart.activity.AuthMainActivity
import com.example.quickmart.databinding.AddressBookLayoutBinding

import com.example.quickmart.databinding.FragmentProfileBinding
import com.example.quickmart.utils.utils
import com.example.quickmart.viewModels.UserViewModel
import com.google.firebase.database.FirebaseDatabase


class ProfileFragment : Fragment() {
    private lateinit var binding : FragmentProfileBinding
    private val viewModel : UserViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater)

        onOrdersBtnClicked()
        onBackBtnClicked()
        phoneNumberShown()
        onAddressBookClicked()
        onLogOutClicked()

        return binding.root
    }

    private fun phoneNumberShown() {
        viewModel.getAdminPhoneNumber {
            binding.userPhoneNumber.text = it.toString()
        }
    }

    private fun onLogOutClicked() {
        binding.logOutBtn.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            val alertDialog = builder.create()
                builder.setTitle("Log Out")
                .setIcon(R.drawable.logout)
                .setMessage("Are you Want to Logout")
                .setPositiveButton("Yes"){_,_->
                    viewModel.LogOutUser()
                    val intent = Intent(requireContext() , AuthMainActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }
                .setNegativeButton("No"){_,_->
                    alertDialog.dismiss()
                }
                    .show()
                    .setCancelable(false)
        }
    }

    private fun onAddressBookClicked() {
        binding.addressBtn.setOnClickListener {
            val addressBookLayoutBinding = AddressBookLayoutBinding.inflate(LayoutInflater.from(requireContext()))
            viewModel.getUserAdressFromFirebase {
                addressBookLayoutBinding.tvChangeAddress.setText(it.toString())
            }
            val alertDialog = AlertDialog.Builder(requireContext())
                .setView(addressBookLayoutBinding.root)
                .create()
            alertDialog.show()

            addressBookLayoutBinding.btnEditProduct.setOnClickListener {
                addressBookLayoutBinding.tvChangeAddress.isEnabled = true
            }
            addressBookLayoutBinding.btnSaveProduct.setOnClickListener {
                viewModel.saveAddressinFireBase(addressBookLayoutBinding.tvChangeAddress.text.toString())
                alertDialog.dismiss()
                utils.showToast(requireContext() , "Address Updated...")
            }
        }
    }

    private fun onOrdersBtnClicked() {
        binding.ordersBtn.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_ordersFragment)
        }
    }

    private fun onBackBtnClicked() {
        binding.profileBackBtn.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_homeFragment)
        }
    }

}