package com.example.setapakhouse

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.*
import kotlinx.android.synthetic.main.activity_main.toolbar
import kotlinx.android.synthetic.main.activity_verify_otp.*
import kotlinx.android.synthetic.main.activity_verify_otp.progressBar
import kotlinx.android.synthetic.main.activity_verify_otp.progressBar2
import java.util.concurrent.TimeUnit

class VerifyOtpActivity : AppCompatActivity() {

    lateinit var mAuth : FirebaseAuth
    lateinit var mAuthVerificationId : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_otp)

        setSupportActionBar(toolbar as Toolbar?)
        supportActionBar?.setTitle("OTP Verification")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mAuth = FirebaseAuth.getInstance()

        mAuthVerificationId = intent.getStringExtra("code1")!!

        val timer = object: CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                countdownText.text = "OTP expired in " + (TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60).toString() + "seconds..."
            }

            override fun onFinish() {
                countdownText.text = "OTP is already expired!!"

                val builder = AlertDialog.Builder(this@VerifyOtpActivity)
                builder.setTitle("OTP is already expired!!")
                builder.setMessage("Click Okay to try again.")

                builder.setNeutralButton("Okay", { dialog: DialogInterface?, which: Int ->
                    val intent = Intent(this@VerifyOtpActivity, VerifyPhoneNumberActivity::class.java)
                    startActivity(intent)
                })
                builder.setCancelable(false)
                builder.show()

            }
        }
        timer.start()

        verifyButton.setOnClickListener {

            progressBar.visibility =  View.VISIBLE
            progressBar2.visibility =  View.VISIBLE
            verifyButton.isEnabled = false
            verifyButton.setBackgroundResource(R.color.transparent)

            var pinviewText = pinview.text.toString().trim()

            if(pinviewText.length!=6){
                pinview.setError("Invalid OTP code!")
                pinview.requestFocus()
            }else{
                var credential = PhoneAuthProvider.getCredential(mAuthVerificationId, pinviewText)

                signInWithPhoneAuthCredential(credential, timer)
            }

        }

    }

    private fun signInWithPhoneAuthCredential(
        credential: PhoneAuthCredential,
        timer: CountDownTimer
    ) {

        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "signInWithCredential:success")

                    timer.cancel()
                    progressBar.visibility =  View.INVISIBLE
                    progressBar2.visibility =  View.INVISIBLE
                    verifyButton.isEnabled = true
                    verifyButton.setBackgroundResource(R.drawable.round_button_blue)

                    val phoneNo =intent.getStringExtra("PhoneNumber")
                    val intent = Intent(this@VerifyOtpActivity, SignUpActivity::class.java)
                    intent.putExtra("PhoneNumber", phoneNo)
                    startActivity(intent)
                    finish()

                } else {
                    Log.d("TAG", "signInWithCredential:failure1", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        Log.d("TAG", "signInWithCredential:failure2", task.exception)

                        progressBar.visibility =  View.INVISIBLE
                        progressBar2.visibility =  View.INVISIBLE
                        verifyButton.isEnabled = true
                        verifyButton.setBackgroundResource(R.drawable.round_button_blue)

                        pinview.setError("Incorrect OTP code!")
                        pinview.requestFocus()
                    }

                }
            }
    }

}