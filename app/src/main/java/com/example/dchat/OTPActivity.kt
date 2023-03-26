package com.example.dchat

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import android.widget.Toast
import com.example.dchat.databinding.ActivityOtpactivityBinding
import com.example.dchat.databinding.ActivityVerificationBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.*
import java.util.concurrent.TimeUnit

class OTPActivity : AppCompatActivity() {

    var binding: ActivityOtpactivityBinding? = null
    var verificationId: String? = null
    var auth: FirebaseAuth? = null
    var dialog: ProgressDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpactivityBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        dialog = ProgressDialog(this@OTPActivity)
        dialog?.setMessage("Sending OTP...")
        dialog?.setCancelable(false)
        dialog?.show()

        auth = FirebaseAuth.getInstance()
        supportActionBar?.hide()
        val phoneNumber = intent.getStringExtra("phoneNumber")
        binding!!.txtPhoneNumber.text = "Verify $phoneNumber"

        val options = PhoneAuthOptions.newBuilder(auth!!)
            .setPhoneNumber(phoneNumber!!)
            .setTimeout(10L, TimeUnit.SECONDS)
            .setActivity(this@OTPActivity)
            .setCallbacks(object : OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                    Toast.makeText(this@OTPActivity, "Success", Toast.LENGTH_SHORT).show()
                }

                override fun onVerificationFailed(p0: FirebaseException) {
                    Toast.makeText(this@OTPActivity, "Failed", Toast.LENGTH_SHORT).show()
                }

                override fun onCodeSent(
                    verifyId: String,
                    forceResendingToken: ForceResendingToken
                ) {
                    super.onCodeSent(verifyId, forceResendingToken)
                    dialog?.dismiss()
                    verificationId = verifyId
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
                    binding!!.otpView.requestFocus()


                }
            }).build()

        verifyPhoneNumber(options)
        binding!!.otpView.setOtpCompletionListener { otp ->
            val credential = getCredential(verificationId!!, otp)
            auth!!.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val intent = Intent(this@OTPActivity, SetupProfileActivity::class.java)
                        startActivity(intent)
                        finishAffinity()
                    } else {
                        Toast.makeText(this@OTPActivity, "Failed", Toast.LENGTH_SHORT).show()
                    }

                }

        }
    }
}