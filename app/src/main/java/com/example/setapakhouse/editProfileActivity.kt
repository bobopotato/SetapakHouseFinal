package com.example.setapakhouse

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.renderscript.Sampler
import android.util.Log
import android.view.MotionEvent
import android.view.animation.Animation
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import com.example.setapakhouse.Model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.activity_edit_profile.fullnameText
import kotlinx.android.synthetic.main.activity_edit_profile.profilePic
import kotlinx.android.synthetic.main.activity_edit_profile.usernameText
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import java.io.IOException
import java.security.AccessController.getContext
import java.util.*


class editProfileActivity : AppCompatActivity() {

    val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
    lateinit var scaleUp: Animation
    lateinit var scaleDown: Animation
    lateinit var storage: FirebaseStorage
    lateinit var storageReference: StorageReference
    lateinit var filePath: Uri
    private var PICK_IMAGE_REQUEST = 1234
    lateinit var ref: DatabaseReference
    lateinit var ref1: DatabaseReference
    lateinit var token: String
    var valid : Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        editSection.setOnClickListener {
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
        }

        editBackBtn.setOnClickListener{
            finish()
        }

        displayInfo()

        scaleUp = android.view.animation.AnimationUtils.loadAnimation(this, R.anim.scale_up)
        scaleDown = android.view.animation.AnimationUtils.loadAnimation(this, R.anim.scale_down)

        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference

        editUploadPhoto.setOnTouchListener { v, event ->
            val action = event.action
            when(action){
                MotionEvent.ACTION_UP->{
                    editUploadPhoto.startAnimation(scaleDown)
                }
                MotionEvent.ACTION_DOWN->{
                    editUploadPhoto.startAnimation(scaleUp)
                    choose()
                }
                else ->{

                }
            }
            true
        }


        //validate when there is changes
        usernameText.editText?.addTextChangedListener{
            validateUsername()
        }

        fullnameText.editText?.addTextChangedListener {
            validateFullName()
        }


        //Continue Submit Button
        doneBtn.setOnClickListener {
            var valid1 = false

            if(validateUsername() && validateFullName()){
                valid1 = true
            }

            if(valid1 == true){
                updateProfile()
                Toast.makeText(this,"UPDATED SUCCESSFUL",Toast.LENGTH_SHORT).show()
                finish()

            }else{
                Toast.makeText(this,"WRONG!!!",Toast.LENGTH_SHORT).show()
            }



        }



    }

    private fun updateProfile() {
        ref=FirebaseDatabase.getInstance().getReference("Users")
        ref.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(h in snapshot.children){
                        if(h.child("userID").getValue().toString().equals(currentUserID)){
                            val user=h.getValue(User::class.java)
                            user!!.username=txtSetUser.text.toString()
                            user!!.fullName=txtSetFull.text.toString()
                            if(valid==true) {
                                upload()
                            }
                            h.getRef().setValue(user)
                        }
                    }
                }
            }

        })
    }

    private fun displayInfo() {
        ref=FirebaseDatabase.getInstance().getReference("Users")
        ref.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(h in snapshot.children){
                        if(h.child("userID").getValue().toString().equals(currentUserID)){
                            txtSetFull.setText(h.child("fullName").getValue().toString())
                            txtSetUser.setText(h.child("username").getValue().toString())
                            Picasso.get().load(h.child("image").getValue().toString()).placeholder(R.drawable.ic_profile).into(profilePic)
                        }
                    }
                }
            }

        })

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

        val alertBox = AlertDialog.Builder(this@editProfileActivity)

        alertBox.setTitle("Error")

        alertBox.setIcon(R.mipmap.ic_launcher)

        alertBox.setNegativeButton("Close"){dialog, which ->
            dialog.dismiss()
        }

        if(valid==true) {

            if (filePath != null) {
                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Uploading...")
                progressDialog.show()

                val imageRef = storageReference!!.child("images/" + UUID.randomUUID().toString())

                imageRef.putFile(filePath!!)
                    .addOnSuccessListener {
                        progressDialog.dismiss()
                        Toast.makeText(
                            applicationContext,
                            "Updated Successfully",
                            Toast.LENGTH_LONG
                        ).show()

                        imageRef.downloadUrl.addOnSuccessListener {
                            Log.d("Testing", "File location success : $it")
                            token = "$it"
                            ref1=FirebaseDatabase.getInstance().getReference("Users")
                            ref1.addValueEventListener(object :ValueEventListener{
                                override fun onCancelled(error: DatabaseError) {
                                    TODO("Not yet implemented")
                                }

                                override fun onDataChange(snapshot: DataSnapshot) {
                                    for(h in snapshot.children){
                                        if(h.child("userID").getValue().toString().equals(currentUserID)){
                                            h.getRef().child("image").setValue(token)
                                        }
                                    }
                                }

                            })


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
}

