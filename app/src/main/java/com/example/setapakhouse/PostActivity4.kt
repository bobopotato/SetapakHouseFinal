package com.example.setapakhouse

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.ContentResolver
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.setapakhouse.Adapter.UploadListAdapter
import com.example.setapakhouse.Model.Property
import com.example.setapakhouse.Model.PropertyImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_post4.*
import kotlinx.android.synthetic.main.activity_post4.toolbar
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class PostActivity4 : AppCompatActivity() {

    private var PICK_IMAGE_REQUEST = 1
    var imageList = ArrayList<Uri>()
    var resolverList = ArrayList<ContentResolver>()
    lateinit var imageUri : Uri
    lateinit var storage: FirebaseStorage
    lateinit var storageReference: StorageReference
    lateinit var token: String
    lateinit var ref1: DatabaseReference
    lateinit var ref2: DatabaseReference
    lateinit var ref3: DatabaseReference
    lateinit var ref4: DatabaseReference
    lateinit var epicDialog : Dialog
    var valid : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post4)

        setSupportActionBar(toolbar as Toolbar?)
        supportActionBar?.setTitle("Post - Final Step")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference

        epicDialog = Dialog(this)

        val currentUser= FirebaseAuth.getInstance().currentUser
        val renterType = intent.getStringExtra("RenterType")!!
        val propertyType = intent.getStringExtra("PropertyType")!!
        val propertyName = intent.getStringExtra("PropertyName")!!
        val location = intent.getStringExtra("Location")!!
        val postCode = intent.getStringExtra("PostCode")!!
        val rentalPrice = intent.getStringExtra("RentalPrice")!!
        val description = intent.getStringExtra("Description")!!
        val accomodation = intent.getStringExtra("Accomodation")!!
        val preference = intent.getStringExtra("Preference")!!


        uploadButton.setOnClickListener {
            choose()
        }

        postButton.setOnClickListener {
            if(valid){
                upload(location, rentalPrice, postCode, accomodation, preference, propertyName, renterType, "available", getTime(), currentUser!!.uid, description, propertyType)
            }
            else{
                showDialog1()
            }

        }


    }

    private fun choose() {
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "SELECT PICTURE"), PICK_IMAGE_REQUEST)
        //Toast.makeText(this@MainActivity, "FAIL 99", Toast.LENGTH_SHORT).show()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //Toast.makeText(this@MainActivity, "FAIL 99", Toast.LENGTH_SHORT).show()

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            valid = true
            if(data!!.clipData != null){
                val imageCount = data.clipData!!.itemCount
                imageList.clear()
                resolverList.clear()
                for(x in 0 until imageCount){

                    imageUri = data.clipData!!.getItemAt(x).uri

                    imageList.add(imageUri)
                    resolverList.add(contentResolver)
                    Log.d("testmou", x.toString())
                }

                val adapter = UploadListAdapter(imageList, resolverList)
                val mLayoutManager = LinearLayoutManager(applicationContext)
                uploadPhotoView.layoutManager = mLayoutManager
                uploadPhotoView.scrollToPosition(imageList.size-1)
                uploadPhotoView.adapter = adapter

                //Toast.makeText(this, "Selected Multiple Files", Toast.LENGTH_SHORT).show()

            }else{
                showDialog1()
                //Toast.makeText(this, "Please Select Files", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun upload(location : String, rentalPrice : String, postCode : String, accomodation : String, preference : String, propertyName : String, renterType : String, status : String, dateTime : String, userID : String, description : String, propertyType : String) {
        ref1 = FirebaseDatabase.getInstance().getReference("PropertyImage")
        ref2 = FirebaseDatabase.getInstance().getReference("Property")
        ref3 = FirebaseDatabase.getInstance().getReference("House")
        ref4 = FirebaseDatabase.getInstance().getReference("Room")

        val propertyID = ref2.push().key.toString()
        val alertBox = AlertDialog.Builder(this@PostActivity4)
        //var uploadCount = 0
        //var uploadSuccess = false

        alertBox.setTitle("Error")

        alertBox.setIcon(R.mipmap.ic_launcher)

        alertBox.setNegativeButton("Close"){dialog, which ->
            dialog.dismiss()
        }

        val storeProperty = Property(
            propertyID,
            location,
            rentalPrice.toDouble(),
            postCode.toInt(),
            accomodation,
            preference,
            propertyName,
            renterType,
            status,
            dateTime,
            userID,
            description,
            propertyType
        )

        ref2.child(propertyID).setValue(storeProperty).addOnCompleteListener {
            //uploadSuccess = true
        }

        if(propertyType == "House"){
            val houseID = ref3.push().key.toString()
            val houseType = intent.getStringExtra("HouseType")!!
            val roomNo = intent.getStringExtra("RoomNo")!!
            val bathRoomNo = intent.getStringExtra("BathRoomNo")!!

            val houseMap=HashMap<String, Any>()
            houseMap["houseID"]=houseID
            houseMap["houseType"]=houseType
            houseMap["numRoom"]=roomNo
            houseMap["numBathroom"]=bathRoomNo
            houseMap["propertyID"]=propertyID

            ref3.child(houseID).setValue(houseMap)
        }
        if(propertyType == "Room"){
            val roomID = ref4.push().key.toString()
            val roomType = intent.getStringExtra("RoomType")!!
            val maxOccupancy = intent.getStringExtra("MaxOccupancy")!!

            val roomMap=HashMap<String, Any>()
            roomMap["roomID"]=roomID
            roomMap["roomType"]=roomType
            roomMap["maxOccupancy"]=maxOccupancy
            roomMap["propertyID"]=propertyID

            ref4.child(roomID).setValue(roomMap)
        }

        if (imageList != null) {
            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Uploading...")
            progressDialog.show()

            for(x in 0 until imageList.size){
                var propertyImageID = ref1.push().key.toString()
                var imageName = "image" + (x+1).toString()
                var thisImage : Uri = imageList.get(x)
                var imageRef = storageReference!!.child("imagesTest/" + UUID.randomUUID().toString())

                imageRef.putFile(thisImage)
                    .addOnSuccessListener {
                        progressDialog.dismiss()
                        imageRef.downloadUrl.addOnSuccessListener {
                            Log.d("Testing", "File location success : $it")
                            token = "$it"
                            //ref.child("image").setValue(token)

                            val storePropertyImage = PropertyImage(
                                propertyImageID,
                                propertyID,
                                imageName,
                                token

                            )

                            ref1.child(propertyImageID).setValue(storePropertyImage).addOnCompleteListener {
                                //uploadCount++;
                                //Log.d("seeCount", imageList.size.toString() + " == " + uploadCount.toString())
                            }

                            Log.d("Success", "success time " + x.toString())

                            //createAccount(userName, fullName, email, password, phoneNo, token)
                        }
                    }
                    .addOnFailureListener {
                        progressDialog.dismiss()
                        //Toast.makeText(applicationContext, "Failed", Toast.LENGTH_SHORT).show()
                        Log.d("Testing", "fail")
                    }
                    .addOnProgressListener { taskSnapshot ->
                        val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
                        progressDialog.setMessage("Uploaded " + progress.toInt() + "%...")
                        showDialog()
                    }

                if((x+1) == imageList.size){
                    //showDialog()
                }
            }


        }else{
            Log.d("zzz", "fail 99")
        }

    }

    private fun getTime(): String {

        val today = LocalDateTime.now(ZoneId.systemDefault())

        return today.format(DateTimeFormatter.ofPattern("d MMM uuuu HH:mm:ss "))
    }

    private fun showDialog(){
        epicDialog.setContentView(R.layout.popup_positive)
        //val closeButton : ImageView = epicDialog.findViewById(R.id.closeBtn)
        val okButton : Button = epicDialog.findViewById(R.id.okBtn)
        val title : TextView = epicDialog.findViewById(R.id.title)
        val content : TextView = epicDialog.findViewById(R.id.content)

        title.text = "Post Successful"
        content.text = "You will be redirected to the main page"

        okButton.setOnClickListener {
            epicDialog.dismiss()
            val intent = Intent(this@PostActivity4, MainActivity::class.java)
            startActivity(intent)
        }
        epicDialog.setCancelable(true)
        epicDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        epicDialog.show()
    }

    private fun showDialog1(){
        epicDialog.setContentView(R.layout.popup_error)
        //val closeButton : ImageView = epicDialog.findViewById(R.id.closeBtn)
        val okButton : Button = epicDialog.findViewById(R.id.okBtn)
        val title : TextView = epicDialog.findViewById(R.id.title)
        val content : TextView = epicDialog.findViewById(R.id.content)

        title.text = "No Picture Selected"
        content.text = "Please select at least 2 photo of your property"

        okButton.setOnClickListener {
            epicDialog.dismiss()
        }
        epicDialog.setCancelable(true)
        epicDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        epicDialog.show()
    }

}


