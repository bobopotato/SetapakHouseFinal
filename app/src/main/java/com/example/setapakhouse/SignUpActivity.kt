package com.example.setapakhouse


import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.activity_sign_up.passwordText
import kotlinx.android.synthetic.main.activity_sign_up.progressBar
import kotlinx.android.synthetic.main.activity_sign_up.progressBar2


class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        setSupportActionBar(toolbar as Toolbar?)
        supportActionBar?.setTitle("Sign Up")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //Click Outside to hide keyboard
        outside.setOnClickListener {
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
        }

        //Password Validation
        val validatePassword = ValidatePassword()
        passwordText.editText?.addTextChangedListener(validatePassword)

        validatePassword.lowerCase.observe(this, Observer { value ->
            displayPasswordSuggestions(value, lowerCaseImg, lowerCaseText)
        })

        validatePassword.upperCase.observe(this, Observer { value ->
            displayPasswordSuggestions(value, upperCaseImg, upperCaseText)
        })

        validatePassword.digit.observe(this, Observer { value ->
            displayPasswordSuggestions(value, digitImg, digitText)
        })

        validatePassword.specialChar.observe(this, Observer { value ->
            displayPasswordSuggestions(value, specialImg, specialText)
        })

        //validate when there is changes
        usernameText.editText?.addTextChangedListener {
            validateUsername()
        }

        fullnameText.editText?.addTextChangedListener {
            validateFullName()
        }

        emailText.editText?.addTextChangedListener {
            emailText.error = null
        }

        passwordText.editText?.addTextChangedListener {
            passwordText.error=null
        }

        confirmPasswordText.editText?.addTextChangedListener {
            confirmPasswordText.error=null
        }

        //Continue Submit Button
        continueButton.setOnClickListener {
            continueButton.isEnabled = false
            progressBar.visibility =  View.VISIBLE
            progressBar2.visibility =  View.VISIBLE
            var valid = false

            if(validateUsername() && validateFullName() && validateEmail()){
                valid = true
            }

            if(valid == true){
                //Validate Password
                var password = passwordText.editText?.text.toString()

                if(password.isEmpty()){
                    passwordText.error="                     Password can't be null"
                }else{
                    passwordText.error=null
                    if(password.contains(" ")){
                        passwordText.error="                     Password can't contain space bar"
                    }else{
                        if(password.length<8){
                            passwordText.error="                     Password must be greater or equal to 8 characters"
                        }
                        else{
                            if(validatePassword.lowerCase.value == 1 && validatePassword.upperCase.value == 1 && validatePassword.digit.value == 1 && validatePassword.specialChar.value == 1){
                                passwordText.error=null

                                if(confirmPasswordText.editText?.text.toString().equals(password)){
                                    //Register here
                                    val phoneNo =intent.getStringExtra("PhoneNumber")
                                    val intent = Intent(this, UploadProfilePictureActivity::class.java)
                                    intent.putExtra("Username", usernameText.editText?.text.toString().trim())
                                    intent.putExtra("FullName", fullnameText.editText?.text.toString().trim())
                                    intent.putExtra("Email", emailText.editText?.text.toString().trim())
                                    intent.putExtra("Password", passwordText.editText?.text.toString().trim())
                                    intent.putExtra("PhoneNumber", phoneNo)
                                    startActivity(intent)
                                    finish()
                                }
                                else{
                                    confirmPasswordText.error="                     The password must be same"
                                }
                            }
                            else{
                                passwordText.error="                     Please fulfill the password requirement"
                            }

                        }
                    }
                }
            }

            if(usernameText.error!=null || fullnameText.error!=null ||emailText.error!=null || passwordText.error!=null || confirmPasswordText.error!=null){
                progressBar.visibility =  View.INVISIBLE
                progressBar2.visibility =  View.INVISIBLE
                continueButton.isEnabled = true
            }

        }

    }

    private fun validateUsername():Boolean{
        var username = usernameText.editText?.text.toString().trim()

        if(username.length>15){
            usernameText.setError("                     Username too long")
            return false
        }

        if(username.isEmpty()){
            usernameText.setError("                     Field can't be empty")
            return false
        }else{
            usernameText.setError(null)
            return true
        }
    }

    private fun validateFullName():Boolean{
        var fullName = fullnameText.editText?.text.toString().trim()

        if(fullName.isEmpty()){
            fullnameText.setError("                     Field can't be empty")
            return false
        }else{
            fullnameText.setError(null)
            return true
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

    private fun displayPasswordSuggestions(value: Int, image: ImageView, text: TextView) {
        if(value == 1){
            text.setTextColor(getResources().getColor(R.color.green))
            image.setImageResource(R.drawable.ic_checked)
        }else{
            text.setTextColor(getResources().getColor(R.color.gray))
            image.setImageResource(R.drawable.ic_unchecked)
        }
    }

}