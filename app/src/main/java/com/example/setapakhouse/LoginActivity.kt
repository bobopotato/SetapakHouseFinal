package com.example.setapakhouse

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
            val intent = Intent(this, VerifyPhoneNumberActivity::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}