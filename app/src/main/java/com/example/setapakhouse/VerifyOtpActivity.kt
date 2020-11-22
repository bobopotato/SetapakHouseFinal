package com.example.setapakhouse

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
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
    lateinit var epicDialog : Dialog
    lateinit var timer : CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_otp)

        setSupportActionBar(toolbar as Toolbar?)
        supportActionBar?.setTitle("OTP Verification")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        epicDialog = Dialog(this)

        mAuth = FirebaseAuth.getInstance()

        mAuthVerificationId = intent.getStringExtra("code1")!!

        timer = object: CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                countdownText.text = "OTP expired in " + (TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60).toString() + " seconds..."
            }

            override fun onFinish() {
                countdownText.text = "OTP is already expired!!"

                showDialog1()

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

    private fun showDialog3(){
        epicDialog.setContentView(R.layout.popup_confirmation)
        //val closeButton : ImageView = epicDialog.findViewById(R.id.closeBtn)
        val yesButton : Button = epicDialog.findViewById(R.id.yesBtn)
        val cancelButton : Button = epicDialog.findViewById(R.id.cancelBtn)
        val title : TextView = epicDialog.findViewById(R.id.title)
        val content : TextView = epicDialog.findViewById(R.id.content)


        title.text = "Stop Progress"
        content.text = "You are in the middle of verifying OTP. Are you sure to cancel this progress?"
        yesButton.text = "Yes"
        yesButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)

        yesButton.setOnClickListener {
            val intent = Intent(this@VerifyOtpActivity, LoginActivity::class.java)
            startActivity(intent)
            timer.cancel()
            finish()
            epicDialog.dismiss()
        }
        cancelButton.setOnClickListener {
            epicDialog.dismiss()
        }
        epicDialog.setCancelable(true)
        epicDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        epicDialog.show()
    }

    private fun showDialog1(){
        epicDialog.setContentView(R.layout.popup_error)
        //val closeButton : ImageView = epicDialog.findViewById(R.id.closeBtn)
        val okButton : Button = epicDialog.findViewById(R.id.okBtn)
        val title : TextView = epicDialog.findViewById(R.id.title)
        val content : TextView = epicDialog.findViewById(R.id.content)

        title.text = "OTP Is Expired"
        content.text = "Click Okay to try again."

        okButton.setOnClickListener {
            epicDialog.dismiss()
            finish()
        }
        epicDialog.setCancelable(true)
        epicDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        epicDialog.show()
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        //Toast.makeText(this, "wtf = back pressed", Toast.LENGTH_SHORT).show()
        //val intent = Intent(this@VerifyOtpActivity, VerifyOtpActivity::class.java)
        //intent.putExtra("code1", mAuthVerificationId)
        //startActivity(intent)
        showDialog3()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == android.R.id.home){
            //Toast.makeText(this, "wtf = back pressed", Toast.LENGTH_SHORT).show()
            //val intent = Intent(this@VerifyOtpActivity, VerifyOtpActivity::class.java)
            //intent.putExtra("code1", mAuthVerificationId)
            //startActivity(intent)
            showDialog3()

        }
        //return super.onOptionsItemSelected(item)
        return true

    }

}