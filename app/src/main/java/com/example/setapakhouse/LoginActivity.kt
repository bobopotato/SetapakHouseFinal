package com.example.setapakhouse

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {
    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        signUpButton.setOnClickListener{
            val intent = Intent(this, VerifyPhoneNumberActivity::class.java)
            startActivity(intent)
        }

        forgotPasswordButton.setOnClickListener{
            val intent = Intent(this, test::class.java)
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
                        Toast.makeText(
                            baseContext,"Please verify your email address.",
                            Toast.LENGTH_LONG
                        ).show()
                        mAuth.signOut()
                    }
                }
                else
                {
                    val message = task.exception!!.toString()
                    Toast.makeText(this,"Error: $message", Toast.LENGTH_LONG).show()
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

    override fun onStart() {
        super.onStart()

        val currentUser= FirebaseAuth.getInstance().currentUser
        if(currentUser!=null){
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()

        }
    }



}