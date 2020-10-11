package com.example.setapakhouse

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_upload_profile_picture.*

class UploadProfilePictureActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_profile_picture)

        val userName = intent.getStringExtra("Username")
        val fullName = intent.getStringExtra("FullName")
        val email = intent.getStringExtra("Email")
        val password = intent.getStringExtra("Password")
        val phoneNo = intent.getStringExtra("PhoneNumber")
        var image = "https://firebasestorage.googleapis.com/v0/b/setapak-house.appspot.com/o/Default_Profile_Picture%2FdefaultProfilePicture.jpg?alt=media&token=e405bcb3-6a4a-4e89-aec8-7773f379674d"


        createButton.setOnClickListener {
            Log.d("username = ",  userName!!)
            Log.d("fullName = ",  fullName!!)
            Log.d("email = ",  email!!)
            Log.d("password = ",  password!!)
            Log.d("phoneNo = ",  phoneNo!!)
            Log.d("image = ",  image)

            createAccount(userName, fullName, email, password, phoneNo, image)
        }


    }

    private fun createAccount(userName : String, fullName : String, email : String, password:String, phoneNo : String, image:String) {

        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("SignUp")
        progressDialog.setMessage("Please wait, this may take a while...")
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()
        val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

        mAuth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener{task ->
                if(task.isSuccessful){
                    saveUserInfo(userName, fullName,email, phoneNo, image, progressDialog)
                }
                else
                {
                    val message = task.exception!!.toString()
                    Toast.makeText(this,"Error: $message", Toast.LENGTH_LONG).show()
                    mAuth.signOut()
                    progressDialog.dismiss()
                }
            }

    }

    private fun saveUserInfo(username: String, fullName : String, email: String, phoneNo: String, image:String, progressDialog: ProgressDialog) {
        val currentUserID= FirebaseAuth.getInstance().currentUser!!.uid
        val usersRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users")

        val userMap=HashMap<String,Any>()
        userMap["userID"]=currentUserID
        userMap["username"]=username
        userMap["fullName"]=fullName
        userMap["email"]=email
        userMap["phoneNumber"]=phoneNo
        userMap["rewardPoint"]=0
        userMap["balance"]=0
        userMap["image"]=image

        usersRef.child(currentUserID).setValue(userMap)
            .addOnCompleteListener{task ->
                if(task.isSuccessful){
                    progressDialog.dismiss()
                    Toast.makeText(this,"Account has been created successfully.", Toast.LENGTH_LONG)
                    val user= FirebaseAuth.getInstance().currentUser
                    user?.sendEmailVerification()
                        ?.addOnCompleteListener{task ->
                            if(task.isSuccessful){
                                FirebaseAuth.getInstance().signOut()

                                val intent = Intent(this,LoginActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                                Toast.makeText(this,"Account created", Toast.LENGTH_SHORT).show()
                                finish()
                            }
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