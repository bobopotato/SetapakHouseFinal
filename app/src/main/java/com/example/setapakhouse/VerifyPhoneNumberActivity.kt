package com.example.setapakhouse

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ActionMode
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.toolbar
import kotlinx.android.synthetic.main.activity_verify_phone_number.*
import java.util.concurrent.TimeUnit

class VerifyPhoneNumberActivity : AppCompatActivity() {

    lateinit var callbacks : PhoneAuthProvider.OnVerificationStateChangedCallbacks
    lateinit var mAuth : FirebaseAuth
    lateinit var phoneNo :String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_phone_number)

        setSupportActionBar(toolbar as Toolbar?)
        supportActionBar?.setTitle("Phone Verification")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mAuth = FirebaseAuth.getInstance()

        generateButton.setOnClickListener {
            phoneNo = "+" + ccp.selectedCountryCode.toString() + phoneText.text.toString()

            progressBar.visibility =  View.VISIBLE
            progressBar2.visibility =  View.VISIBLE
            generateButton.isEnabled = false
            generateButton.setBackgroundResource(R.color.transparent)

            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNo, // Phone number to verify
                60, // Timeout duration
                TimeUnit.SECONDS, // Unit of timeout
                this, // Activity (for callback binding)
                callbacks)
        }

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(p0, p1)
                progressBar.visibility =  View.INVISIBLE
                progressBar2.visibility =  View.INVISIBLE
                generateButton.isEnabled = true
                generateButton.setBackgroundResource(R.drawable.round_button_blue)
                val intent = Intent(this@VerifyPhoneNumberActivity, VerifyOtpActivity::class.java)
                intent.putExtra("code1", p0)
                intent.putExtra("PhoneNumber", phoneNo)
                startActivity(intent)

            }

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.d("ABCC", "OKAYy")
            }

            override fun onVerificationFailed(e: FirebaseException) {

                if(e.toString().contains("TooManyRequests")){
                    phoneText.setError("This number requested too many times!")
                }else{
                    phoneText.setError("Invalid phone number!" + e.toString())
                }
                phoneText.requestFocus()
                progressBar.visibility =  View.INVISIBLE
                progressBar2.visibility =  View.INVISIBLE
                generateButton.isEnabled = true
                generateButton.setBackgroundResource(R.drawable.round_button_blue)



            }

        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        Toast.makeText(this, "wtf = back pressed", Toast.LENGTH_SHORT).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == android.R.id.home){
            Toast.makeText(this, "wtf = back pressed", Toast.LENGTH_SHORT).show()
        }
        return super.onOptionsItemSelected(item)
    }
}