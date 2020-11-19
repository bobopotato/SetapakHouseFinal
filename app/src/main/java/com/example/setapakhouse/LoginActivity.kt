package com.example.setapakhouse

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {

    lateinit var epicDialog : Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        epicDialog = Dialog(this)

        signUpButton.setOnClickListener{
            val intent = Intent(this, VerifyPhoneNumberActivity::class.java)
            startActivity(intent)
        }

        forgotPasswordButton.setOnClickListener{
            val intent = Intent(this, ForgetPasswordActivity::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener {
            loginUser()
        }
    }

    private fun loginUser() {
        val email= emailText.editText?.text.toString()
        val password= passwordText.editText?.text.toString()

        if(validateEmail() && password.isNotEmpty()){
            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Login")
            progressDialog.setMessage("Please wait, this may take a while...")
            progressDialog.setCanceledOnTouchOutside(false)
            progressDialog.show()

            val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener{task->
                if(task.isSuccessful){
                    if(mAuth.currentUser!!.isEmailVerified){
                        progressDialog.dismiss()
                        val intent = Intent(this, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    }else{
                        progressDialog.dismiss()
                        //Toast.makeText(baseContext,"Please verify your email address.",Toast.LENGTH_LONG).show()
                        showDialog1()
                        mAuth.signOut()
                    }
                }
                else
                {
                    val message = task.exception!!.toString()
                    //Toast.makeText(this,"Error: $message", Toast.LENGTH_LONG).show()
                    showDialog2()
                    FirebaseAuth.getInstance().signOut()
                    progressDialog.dismiss()
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

    private fun showDialog1(){
        epicDialog.setContentView(R.layout.popup_error)
        //val closeButton : ImageView = epicDialog.findViewById(R.id.closeBtn)
        val okButton : Button = epicDialog.findViewById(R.id.okBtn)
        val title : TextView = epicDialog.findViewById(R.id.title)
        val content : TextView = epicDialog.findViewById(R.id.content)

        title.text = "Email Haven't Verified"
        content.text = "Please go to your email to verify your newly registered account"

        okButton.setOnClickListener {
            epicDialog.dismiss()
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

        title.text = "Invalid Email or Password"
        content.text = "Your email or password is incorrect. Please try again."

        okButton.setOnClickListener {
            epicDialog.dismiss()
        }
        epicDialog.setCancelable(true)
        epicDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        epicDialog.show()

    }

}