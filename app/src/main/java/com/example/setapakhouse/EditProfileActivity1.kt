package com.example.setapakhouse

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.Color.rgb
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import com.example.setapakhouse.Model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_edit_profile1.*
import kotlinx.android.synthetic.main.activity_edit_profile1.backBtn
import kotlinx.android.synthetic.main.activity_edit_profile1.fullnameText
import kotlinx.android.synthetic.main.activity_edit_profile1.usernameText
import java.io.IOException
import java.util.*

class EditProfileActivity1 : AppCompatActivity() {

    lateinit var storage: FirebaseStorage
    lateinit var storageReference: StorageReference
    lateinit var filePath: Uri
    private var PICK_IMAGE_REQUEST = 1234
    lateinit var ref: DatabaseReference
    lateinit var ref1: DatabaseReference
    lateinit var token: String
    lateinit var epicDialog : Dialog
    var valid : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile1)

        val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid

        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference

        epicDialog = Dialog(this)

        ref = FirebaseDatabase.getInstance().getReference("Users").child(currentUserID)
        ref1 = FirebaseDatabase.getInstance().getReference("Users")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    Picasso.get().load(p0.child("image").getValue().toString()).placeholder(R.drawable.profile).into(profilePic)
                    usernameText.editText?.setText(p0.child("username").getValue().toString())
                    fullnameText.editText?.setText(p0.child("fullName").getValue().toString())
                }
            }
        })




        usernameText.editText?.addTextChangedListener {
            var username1 = usernameText.editText?.text.toString().trim()
            var exist = "false"

            ref1.addValueEventListener(object: ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for(h in snapshot.children){
                            if(h.child("username").getValue().toString().equals(username1) && h.child("userID").getValue().toString()!=currentUserID){
                                exist = "true"
                                hiddenExist.text = exist
                                Log.d("asdada", exist)
                                validateUsername(currentUserID)
                            }
                            else{
                                hiddenExist.text = exist
                                validateUsername(currentUserID)
                            }

                        }

                    }
                }
            })
            validateUsername(currentUserID)
        }

        fullnameText.editText?.addTextChangedListener {
            validateFullName()
        }


        changeProfilePhotoClick.setOnClickListener {
            choose()
        }

        submitChangeBtn.setOnClickListener {
            var username = usernameText.editText?.text.toString().trim()
            var fullName = fullnameText.editText?.text.toString().trim()

            if(valid && validateUsername(currentUserID) && validateFullName()){
                upload(username, fullName)
                Log.d("aaaa", "asdad")
            }
            if(!valid && validateUsername(currentUserID) && validateFullName()){
                ref.child("username").setValue(username)
                ref.child("fullName").setValue(fullName)
                showDialog()
            }


        }

        changePasswordBtn.setOnClickListener {
            val intent = Intent(this, changepwaActivity::class.java)
            startActivity(intent)
        }

        backBtn.setOnClickListener {
            finish()
        }

    }


    private fun validateUsername(currentUserID : String):Boolean{
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

    private fun upload(username : String, fullName : String) {

        val alertBox = AlertDialog.Builder(this@EditProfileActivity1)

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
                            //Log.d("Testing", "File location success : $it")
                            token = "$it"
                            ref.child("image").setValue(token)
                            ref.child("username").setValue(username)
                            ref.child("fullName").setValue(fullName)
                            //show success message
                            showDialog()
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

    private fun showDialog(){
        epicDialog.setContentView(R.layout.popup_positive)
        //val closeButton : ImageView = epicDialog.findViewById(R.id.closeBtn)
        val okButton : Button = epicDialog.findViewById(R.id.okBtn)
        val title : TextView = epicDialog.findViewById(R.id.title)
        val content : TextView = epicDialog.findViewById(R.id.content)

        title.text = "Edit Successful"
        content.text = "Your profile has been updated successfully"

        okButton.setOnClickListener {
            epicDialog.dismiss()
            finish()
        }
        epicDialog.setCancelable(true)
        epicDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        epicDialog.show()

    }
}