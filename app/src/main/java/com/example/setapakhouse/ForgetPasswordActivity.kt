package com.example.setapakhouse

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.addTextChangedListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DatabaseReference
import kotlinx.android.synthetic.main.activity_forget_password.*
import kotlinx.android.synthetic.main.activity_forget_password.progressBar2
import kotlinx.android.synthetic.main.activity_forget_password.toolbar



class ForgetPasswordActivity : AppCompatActivity() {

    lateinit var ref : DatabaseReference
    lateinit var epicDialog : Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_password)

        setSupportActionBar(toolbar as Toolbar?)
        supportActionBar?.setTitle("Forgot Password")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        epicDialog = Dialog(this)

        emailText.editText?.addTextChangedListener {
            emailText.error=null
        }

        sendRecoveryBtn.setOnClickListener {

            if(validateEmail()){
                sendRecoveryBtn.isEnabled = false
                sendRecoveryBtn.setBackgroundResource(R.color.transparent)
                progressBar2.visibility =  View.VISIBLE
                val recoveryEmail = emailText.editText?.text.toString().trim()

                FirebaseAuth.getInstance().sendPasswordResetEmail(recoveryEmail)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            showDialog()
                            progressBar2.visibility =  View.INVISIBLE
                            sendRecoveryBtn.isEnabled = true
                            sendRecoveryBtn.setBackgroundResource(R.drawable.round_button_blue)
                        }
                        else{
                            Log.d("email", task.exception.toString())
                            showDialog2()
                            progressBar2.visibility =  View.INVISIBLE
                            sendRecoveryBtn.isEnabled = true
                            sendRecoveryBtn.setBackgroundResource(R.drawable.round_button_blue)
                        }
                    }
            }
        }

    }

    private fun validateEmail():Boolean{
        val EMAIL_REGEX = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
        var email = emailText.editText?.text.toString().trim()

        if(email.isEmpty()){
            emailText.setError("                     Field can't be empty")
            return false
        }else{
            if(EMAIL_REGEX.toRegex().matches(email)){
                emailText.setError(null)
                return true
            }else{
                emailText.setError("                     Please enter a valid email address")
                return false
            }
        }

    }

    private fun showDialog(){
        epicDialog.setContentView(R.layout.popup_positive)
        //val closeButton : ImageView = epicDialog.findViewById(R.id.closeBtn)
        val okButton : Button = epicDialog.findViewById(R.id.okBtn)
        val title : TextView = epicDialog.findViewById(R.id.title)
        val content : TextView = epicDialog.findViewById(R.id.content)

        title.text = "Password Recovery"
        content.text = "Recovery email has been sent to your email"

        okButton.setOnClickListener {
            epicDialog.dismiss()
            finish()
        }
        epicDialog.setCancelable(true)
        epicDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        epicDialog.show()

    }

    private fun showDialog2(){
        epicDialog.setContentView(R.layout.popup_error)
        //val closeButton : ImageView = epicDialog.findViewById(R.id.closeBtn)
        val okButton : Button = epicDialog.findViewById(R.id.okBtn)
        val title : TextView = epicDialog.findViewById(R.id.title)
        val content : TextView = epicDialog.findViewById(R.id.content)

        title.text = "Invalid Email"
        content.text = "This email is not exist in Setapak House. Please enter an existing email."

        okButton.setOnClickListener {
            epicDialog.dismiss()
        }
        epicDialog.setCancelable(true)
        epicDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        epicDialog.show()

    }
}