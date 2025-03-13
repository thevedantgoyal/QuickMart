package com.example.quickmart.viewModels

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.quickmart.models.Users
import com.example.quickmart.utils.utils
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.concurrent.TimeUnit

class AuthViewModel : ViewModel() {

     lateinit var mauth : FirebaseAuth
    private val _verificationId = MutableStateFlow<String?>(null)





    private val _sendOtp = MutableStateFlow(false)
    val otpSent = _sendOtp


    private var _isSignedSuccessfully = MutableStateFlow(false)
    val signedSuccessfully = _isSignedSuccessfully


    private val _isCurrentUser = MutableStateFlow(false)
    val currentUser = _isCurrentUser


    init {
        utils.getAuthInstance().currentUser?.let {
            _isCurrentUser.value = true
        }
    }

    fun sendOtp(userNumber : String , activity : Activity) {

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {

            }

            override fun onVerificationFailed(e: FirebaseException) {

            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken,
            ) {

                _verificationId.value = verificationId
                _sendOtp.value = true
            }
        }

        val options = PhoneAuthOptions.newBuilder(utils.getAuthInstance())
            .setPhoneNumber("+91${userNumber}") // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(activity) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

     fun signInWithPhoneAuthCredential(otp: String, userNumber: String) {
        val credential = PhoneAuthProvider.getCredential(_verificationId.value.toString(), otp)
         Log.d("otp" , "credential : ${credential}")
         Log.d("otp" , "verification : ${_verificationId.value.toString()}")
           utils.getAuthInstance().signInWithCredential(credential)
            .addOnCompleteListener{task->
                if(task.isSuccessful){
                    Log.d("otp" ,"task is successful : ${task.isSuccessful.toString()}")
                    val user = task.result?.user
                    Log.d("otp" ,"user : ${user}")
                    val userUid = user?.uid
                    val userfirebase = Users(
                        uid = userUid,
                        userPhoneNumber = userNumber,
                        userAddress = " "
                    )
                    Log.d("otp" , "userUId = ${userUid}")
                    if (userUid != null) {
                        FirebaseDatabase.getInstance().getReference("AllUsers").child("Users").child(userUid).setValue(userfirebase)
                    }
                    else{
                        Log.d("otp" , "useruid is null firebase not uploaded")
                    }
                    _isSignedSuccessfully.value = true

                }else{
                    Log.d("signed" , "failed : ${task.exception?.message}")
                }
            }.addOnFailureListener {exception->
                Log.d("otp" , "message : ${exception.message}")
               }
    }


}