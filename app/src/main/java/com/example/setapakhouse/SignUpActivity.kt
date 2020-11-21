package com.example.setapakhouse


import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.activity_sign_up.fullnameText
import kotlinx.android.synthetic.main.activity_sign_up.usernameText


class SignUpActivity : AppCompatActivity() {

    lateinit var ref : DatabaseReference
    lateinit var ref1: DatabaseReference
    lateinit var createListener : ValueEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        setSupportActionBar(toolbar as Toolbar?)
        supportActionBar?.setTitle("Sign Up")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        var image = "https://firebasestorage.googleapis.com/v0/b/setapak-house.appspot.com/o/Default_Profile_Picture%2Fprofile.png?alt=media&token=2bd74889-561a-488d-982c-e29e56f17b3e"

        ref = FirebaseDatabase.getInstance().getReference("Users")
        ref1 = FirebaseDatabase.getInstance().getReference("Users")

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
            var username1 = usernameText.editText?.text.toString().trim()
            var exist = "false"

            createListener = ref1.addValueEventListener(object: ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for(h in snapshot.children){
                            if(h.child("username").getValue().toString().equals(username1)){
                                exist = "true"
                                hiddenExist.text = exist
                                validateUsername()
                            }
                            else{
                                hiddenExist.text = exist
                                validateUsername()
                            }

                        }

                    }
                }
            })
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
        createAccButton.setOnClickListener {
            ref1.removeEventListener(createListener)
            createAccButton.isEnabled = false
            createAccButton.setBackgroundResource(R.color.transparent)
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
                                    val username = usernameText.editText?.text.toString().trim()
                                    val fullname = fullnameText.editText?.text.toString().trim()
                                    val email = emailText.editText?.text.toString().trim()
                                    val password = passwordText.editText?.text.toString().trim()
                                    val phoneNo =intent.getStringExtra("PhoneNumber")

                                    createAccount(username,fullname,email,password,phoneNo,image)

                                    //val intent = Intent(this, UploadProfilePictureActivity::class.java)
                                    //intent.putExtra("Username", usernameText.editText?.text.toString().trim())
                                    //intent.putExtra("FullName", fullnameText.editText?.text.toString().trim())
                                    //intent.putExtra("Email", emailText.editText?.text.toString().trim())
                                    //intent.putExtra("Password", passwordText.editText?.text.toString().trim())
                                    //intent.putExtra("PhoneNumber", phoneNo)
                                    //startActivity(intent)
                                    //finish()
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
                createAccButton.isEnabled = true
                createAccButton.setBackgroundResource(R.drawable.round_button_blue)
            }

        }

    }

    private fun validateUsername():Boolean{
        var username = usernameText.editText?.text.toString().trim()
        usernameText.setError(null)

        if(hiddenExist.text.equals("true")){
            usernameText.setError("                     This username is being taken")
            return false
        }

        if(username.contains(" ")){
            usernameText.setError("                     Username can't contain empty space")
            return false
        }


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

    private fun createAccount(
        userName: String,
        fullName: String,
        email: String,
        password: String,
        phoneNo: String,
        image: String
    ) {

        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("SignUp")
        progressDialog.setMessage("Please wait, this may take a while...")
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()
        val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener{ task ->
                if(task.isSuccessful){
                    saveUserInfo(userName, fullName, email, phoneNo, image, progressDialog)
                }
                else
                {
                    val message = task.exception!!.toString()
                    //Toast.makeText(this, "Error: $message", Toast.LENGTH_LONG).show()
                    mAuth.signOut()
                    progressDialog.dismiss()
                    emailText.setError("                     This email is already being used!")
                    emailText.requestFocus()
                    progressBar.visibility =  View.INVISIBLE
                    progressBar2.visibility =  View.INVISIBLE
                    createAccButton.setBackgroundResource(R.drawable.round_button_blue)
                    createAccButton.isEnabled = true
                }
            }

    }

    private fun saveUserInfo(
        username: String,
        fullName: String,
        email: String,
        phoneNo: String,
        image: String,
        progressDialog: ProgressDialog
    ) {
        val currentUserID= FirebaseAuth.getInstance().currentUser!!.uid
        val usersRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users")

        val userMap=HashMap<String, Any>()
        userMap["userID"]=currentUserID
        userMap["username"]=username
        userMap["fullName"]=fullName
        userMap["email"]=email
        userMap["phoneNumber"]=phoneNo
        userMap["rewardPoint"]=0
        userMap["balance"]=0
        userMap["image"]=image

        usersRef.child(currentUserID).setValue(userMap)
            .addOnCompleteListener{ task ->
                if(task.isSuccessful){
                    progressDialog.dismiss()
                    Toast.makeText(
                        this,
                        "Account has been created successfully.",
                        Toast.LENGTH_LONG
                    )
                    val user= FirebaseAuth.getInstance().currentUser
                    user?.sendEmailVerification()
                        ?.addOnCompleteListener{ task ->
                            if(task.isSuccessful){
                                FirebaseAuth.getInstance().signOut()

                                val intent = Intent(this, UploadProfilePictureActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                intent.putExtra("UserID", currentUserID)
                                intent.putExtra("UserName", username)
                                startActivity(intent)
                                Toast.makeText(this, "Account created", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                        }
                }
                else
                {
                    val message = task.exception!!.toString()
                    //Toast.makeText(this, "Error: $message", Toast.LENGTH_LONG).show()
                    FirebaseAuth.getInstance().signOut()
                    progressDialog.dismiss()

                }
            }
    }

}