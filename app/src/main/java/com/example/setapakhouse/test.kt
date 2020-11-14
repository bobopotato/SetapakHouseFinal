package com.example.setapakhouse

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.setapakhouse.Adapter.UploadListAdapter
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_test.*
import java.util.*


class test : AppCompatActivity() {


    private var PICK_IMAGE_REQUEST = 1
    var imageList = ArrayList<Uri>()
    var resolverList = ArrayList<ContentResolver>()
    lateinit var imageUri : Uri
    lateinit var storage: FirebaseStorage
    lateinit var storageReference: StorageReference
    lateinit var token: String
    lateinit var ref1: DatabaseReference
    lateinit var ref2: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        //ref = FirebaseDatabase.getInstance().getReference("Users")
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference



        Places.initialize(this, "AIzaSyBAtADCWISs8VHLbeNRZq2Kk1jPBEGVpRg")

        abc.setFocusable(false)

        abc.setOnClickListener {
            var fieldList : List<Place.Field>  = Arrays.asList(
                Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME
            )

            val intent = Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY, fieldList
            ).build(this)

            startActivityForResult(intent, 100)


        }

        table1.setOnClickListener {
            choose()


        }




        table2.setOnClickListener {
            //upload()
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

                Toast.makeText(this, "Selected Multiple Files", Toast.LENGTH_SHORT).show()

            }else{

                Toast.makeText(this, "Please Select Files", Toast.LENGTH_SHORT).show()

            }

        }
    }

    /*private fun upload() {
        ref1 = FirebaseDatabase.getInstance().getReference("PropertyImage")
        ref2 = FirebaseDatabase.getInstance().getReference("Property")
        val propertyID = ref2.push().key.toString()
        val alertBox = AlertDialog.Builder(this@test)
        var uploadCount = 0
        var uploadSuccess = false

        alertBox.setTitle("Error")

        alertBox.setIcon(R.mipmap.ic_launcher)

        alertBox.setNegativeButton("Close"){dialog, which ->
            dialog.dismiss()
        }


            if (imageList != null) {
                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Uploading...")
                progressDialog.show()

                for(x in 0 until imageList.size){
                    var propertyImageID = ref1.push().key.toString()
                    var imageName = "image" + x.toString()
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

                                val storeProperty = Property(
                                    propertyImageID,
                                    propertyID,
                                    imageName,
                                    token

                                )

                                ref1.child(propertyImageID).setValue(storePropertyImage).addOnCompleteListener {
                                    uploadCount++;
                                }

                                ref1.child(propertyID).setValue(storePropertyImage).addOnCompleteListener {
                                    uploadCount++;
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
                            val progress =
                                100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
                            progressDialog.setMessage("Uploaded " + progress.toInt() + "%...")
                        }
                }

                if(uploadCount == imageList.size){
                    val builder = AlertDialog.Builder(this@test)
                    builder.setTitle("Successful Post")
                    builder.setMessage("You will be redirected to the main page.")

                    builder.setNeutralButton("Okay", { dialog: DialogInterface?, which: Int ->
                        val intent = Intent(this@test, LoginActivity::class.java)
                        startActivity(intent)
                    })
                    builder.setCancelable(false)
                    builder.show()
                }

        }else{
                Log.d("zzz", "fail 99")
            }

    }*/

}