package com.example.quickmart.utils

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.quickmart.activity.OrderPlacedActivity
import com.example.quickmart.databinding.ProgresDialogBinding
import com.google.firebase.auth.FirebaseAuth
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

object utils {

    private var dialog : AlertDialog? = null
    private lateinit var binding : ProgresDialogBinding

    fun showDialog(context: Context , message: String){
      binding = ProgresDialogBinding.inflate(LayoutInflater.from(context))
        binding.tvMessage.text = message
        dialog = AlertDialog.Builder(context).setView(binding.root).setCancelable(false).create()
        dialog!!.show()

    }

    fun hideDialog(){
        dialog?.dismiss()
    }

    fun showToast(context: Context, message:String){
        Toast.makeText(context , message,Toast.LENGTH_SHORT).show()
    }


    private var firebaseInstance : FirebaseAuth? =  null
      fun getAuthInstance() : FirebaseAuth{
          if(firebaseInstance == null){
              firebaseInstance = FirebaseAuth.getInstance()
              Log.d("otp" , "firebase instance : ${firebaseInstance}")
          }
          return firebaseInstance!!
    }

    fun getCurrentUserid() : String?{
        return FirebaseAuth.getInstance().currentUser?.uid
    }

    fun randomId() : String{
        return (1..20).map { (('A'..'Z') + ('a'..'z') + ('0'..'9')).random() }.joinToString("")
    }

    fun getCurrentDate() : String?{
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        return currentDate.format(formatter)
    }

}