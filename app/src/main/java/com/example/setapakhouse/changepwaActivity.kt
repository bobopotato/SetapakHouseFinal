package com.example.setapakhouse

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_changepwa.*
import kotlinx.android.synthetic.main.activity_changepwa.confirmPasswordText
import kotlinx.android.synthetic.main.activity_changepwa.digitImg
import kotlinx.android.synthetic.main.activity_changepwa.digitText
import kotlinx.android.synthetic.main.activity_changepwa.lowerCaseImg
import kotlinx.android.synthetic.main.activity_changepwa.lowerCaseText
import kotlinx.android.synthetic.main.activity_changepwa.passwordText
import kotlinx.android.synthetic.main.activity_changepwa.specialImg
import kotlinx.android.synthetic.main.activity_changepwa.specialText
import kotlinx.android.synthetic.main.activity_changepwa.upperCaseImg
import kotlinx.android.synthetic.main.activity_changepwa.upperCaseText
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_changepwa.progressBar2



class changepwaActivity : AppCompatActivity() {

    lateinit var ref : DatabaseReference
    lateinit var epicDialog : Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_changepwa)

        epicDialog = Dialog(this)

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


        passwordText.editText?.addTextChangedListener {
            passwordText.error=null
        }

        confirmPasswordText.editText?.addTextChangedListener {
            confirmPasswordText.error=null
        }

        var auth= FirebaseAuth.getInstance()


        changePasswordButton.setOnClickListener {
            changePasswordButton.isEnabled = false
            changePasswordButton.setBackgroundResource(R.color.transparent)
            progressBar2.visibility =  View.VISIBLE

            var password = passwordText.editText?.text.toString()
            var validPassword = false
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
                                validPassword = true
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

            if(validPassword){
                val oldPassword = oldPasswordText.editText?.text.toString()
                val user=auth.currentUser
                val credential = EmailAuthProvider
                    .getCredential(user!!.email!!, oldPassword)
                // Prompt the user to re-provide their sign-in credentials
                user?.reauthenticate(credential)
                    ?.addOnCompleteListener {
                        if(it.isSuccessful){
                            user?.updatePassword(password)
                                ?.addOnCompleteListener{task ->
                                    if(task.isSuccessful){
                                        showDialog1()
                                    }
                                }
                        }else{
                            Log.d("password", it.exception.toString())
                            showDialog2()
                            oldPasswordText.error="                     Your current password is incorrect"
                            progressBar2.visibility =  View.INVISIBLE
                            changePasswordButton.isEnabled = true
                            changePasswordButton.setBackgroundResource(R.drawable.round_button_blue)
                        }
                    }
            }
            else{
                progressBar2.visibility =  View.INVISIBLE
                changePasswordButton.isEnabled = true
                changePasswordButton.setBackgroundResource(R.drawable.round_button_blue)
            }


        }

        backBtn.setOnClickListener {
            finish()
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

    private fun showDialog1(){
        epicDialog.setContentView(R.layout.popup_positive)
        //val closeButton : ImageView = epicDialog.findViewById(R.id.closeBtn)
        val okButton : Button = epicDialog.findViewById(R.id.okBtn)
        val title : TextView = epicDialog.findViewById(R.id.title)
        val content : TextView = epicDialog.findViewById(R.id.content)

        title.text = "Reset Password Success"
        content.text = "Your account password has been updated successfully."

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

        title.text = "Reset Password Fail"
        content.text = "Your current password is incorrect. Please try again."

        okButton.setOnClickListener {
            epicDialog.dismiss()
        }
        epicDialog.setCancelable(true)
        epicDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        epicDialog.show()
    }
}