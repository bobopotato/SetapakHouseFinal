package com.example.setapakhouse

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.animation.AnimationUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_upload_profile_picture.*
import java.io.IOException
import java.util.*
import kotlin.collections.HashMap


class UploadProfilePictureActivity : AppCompatActivity() {

    lateinit var scaleUp: Animation
    lateinit var scaleDown: Animation
    lateinit var storage: FirebaseStorage
    lateinit var storageReference: StorageReference
    lateinit var filePath: Uri
    private var PICK_IMAGE_REQUEST = 1234
    lateinit var ref: DatabaseReference
    lateinit var token: String
    var valid : Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_profile_picture)

        setSupportActionBar(toolbar as Toolbar?)
        supportActionBar?.setTitle("Upload Profile Picture")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        scaleUp = android.view.animation.AnimationUtils.loadAnimation(this, R.anim.scale_up)
        scaleDown = android.view.animation.AnimationUtils.loadAnimation(this, R.anim.scale_down)

        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference
        val userID = intent.getStringExtra("UserID")
        val userName = intent.getStringExtra("Username")

        ref = FirebaseDatabase.getInstance().getReference("Users").child(userID)
        username.setText(userName)

        uploadPhoto.setOnTouchListener { v, event ->
            val action = event.action
            when(action){
                MotionEvent.ACTION_UP->{
                    uploadPhoto.startAnimation(scaleDown)
                }
                MotionEvent.ACTION_DOWN->{
                    uploadPhoto.startAnimation(scaleUp)
                    choose()
                }
                else ->{

                }
            }
            true
        }

        createButton.setOnClickListener {
            upload()
        }

        skipButton.setOnClickListener {
            returnLoginPage()
        }


    }

    private fun choose() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "SELECT PICTURE"), PICK_IMAGE_REQUEST)
        //Toast.makeText(this@MainActivity, "FAIL 99", Toast.LENGTH_SHORT).show()

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //Toast.makeText(this@MainActivity, "FAIL 99", Toast.LENGTH_SHORT).show()

        if (requestCode == PICK_IMAGE_REQUEST &&
            resultCode == Activity.RESULT_OK &&
            data != null && data.data!! != null
        ) {
            valid = true
            filePath = data.data!!
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                profilePic!!.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
                //Toast.makeText(this, "FAIL 99", Toast.LENGTH_SHORT).show()
            }

        }
    }
//
//
//
//
//
    private fun upload() {

        val alertBox = AlertDialog.Builder(this@UploadProfilePictureActivity)

        alertBox.setTitle("Error")

        alertBox.setIcon(R.mipmap.ic_launcher)

        alertBox.setNegativeButton("Close"){dialog, which ->
            dialog.dismiss()
        }

        if(valid == false){
            alertBox.setMessage("Please choose a photo")
            alertBox.show()
        }
        else {

            if (filePath != null) {
                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Uploading...")
                progressDialog.show()

                val imageRef = storageReference!!.child("images/" + UUID.randomUUID().toString())

                imageRef.putFile(filePath!!)
                    .addOnSuccessListener {
                        progressDialog.dismiss()
                        imageRef.downloadUrl.addOnSuccessListener {
                            Log.d("Testing", "File location success : $it")
                            token = "$it"
                            ref.child("image").setValue(token)
                            val builder = AlertDialog.Builder(this@UploadProfilePictureActivity)
                            builder.setTitle("Successful Registration")
                            builder.setMessage("You will be redirected to the login page.")

                            builder.setNeutralButton("Okay", { dialog: DialogInterface?, which: Int ->
                                val intent = Intent(this@UploadProfilePictureActivity, LoginActivity::class.java)
                                startActivity(intent)
                            })
                            builder.setCancelable(false)
                            builder.show()

                            //createAccount(userName, fullName, email, password, phoneNo, token)
                        }
                    }
                    .addOnFailureListener {
                        progressDialog.dismiss()
                        //Toast.makeText(applicationContext, "Failed", Toast.LENGTH_SHORT).show()
                        Log.d("Testing", "fail")
                    }
                    .addOnProgressListener { taskSnapshot ->
                        val progress =
                            100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
                        progressDialog.setMessage("Uploaded " + progress.toInt() + "%...")
                    }


            }
        }

    }

    private fun returnLoginPage(){
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    /*private fun createAccount(
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
                    Toast.makeText(this, "Error: $message", Toast.LENGTH_LONG).show()
                    mAuth.signOut()
                    progressDialog.dismiss()
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

                                val intent = Intent(this, LoginActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                                Toast.makeText(this, "Account created", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                        }
                }
                else
                {
                    val message = task.exception!!.toString()
                    Toast.makeText(this, "Error: $message", Toast.LENGTH_LONG).show()
                    FirebaseAuth.getInstance().signOut()
                    progressDialog.dismiss()
                }
            }
    }*/
}