package com.example.setapakhouse

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.setapakhouse.Adapter.HomeAdapter
import com.example.setapakhouse.Fragment.ProfileFragment
import com.example.setapakhouse.Model.Property
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_other_user_profile.*



class OtherUserProfileActivity : AppCompatActivity() {
    lateinit var ref: DatabaseReference
    lateinit var ref1: DatabaseReference
    lateinit var propertyList : MutableList<Property>
    lateinit var otherUserID:String
    lateinit var propertyIDList:MutableList<Property>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_other_user_profile)
        var settings: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)




        propertyIDList= mutableListOf()
        propertyList= mutableListOf()
        otherUserID=intent.getStringExtra("otherUserID")
        var editor: SharedPreferences.Editor=settings.edit()
        editor.putString("checkUserIDHome",otherUserID)
        editor.putString("clickOrNot","yes")
        editor.commit()
        displayProfile()
        addToList()
        otherUserBackBtn.setOnClickListener {
            finish()
        }
    }

    private fun addToList(){
        ref = FirebaseDatabase.getInstance().getReference("Property")
        ref.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    propertyList.clear()
                    for (h in snapshot.children){
                        if(h.child("status").getValue().toString().equals("available")&&
                            h.child("userID").getValue().toString().equals(otherUserID)) {
                            val property = h.getValue(Property::class.java)
                            propertyList.add(property!!)
                            //Log.d(tag,propertyList.toString())
                        }

                    }

                    if(propertyList.size == 0){
                        noRecordFound.visibility = View.VISIBLE
                    }
                    else{
                        noRecordFound.visibility = View.GONE
                    }

                    val mLayoutManager = LinearLayoutManager(this@OtherUserProfileActivity)
                    mLayoutManager.reverseLayout = true

                    userPost.layoutManager = mLayoutManager
                    userPost.scrollToPosition(propertyList.size-1)
                    userPost.adapter = HomeAdapter(propertyList,false)
                }
            }

        })

    }
    private fun displayProfile() {
        ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (h in snapshot.children) {
                        if (h.child("userID").getValue().toString().equals(otherUserID)) {
                            Picasso.get().load(h.child("image").getValue().toString())
                                .placeholder(R.drawable.ic_profile).into(imgProfile)
                            usernameTxt.text = h.child("username").getValue().toString()
                            txtFullName.text = h.child("fullName").getValue().toString()
                            txtEmail.text = h.child("email").getValue().toString()
                            txtPhone.text = h.child("phoneNumber").getValue().toString()
                        }

                    }
                }
            }

        })

        var totalRating:Double
        var countRating:Int


        readData(object: OtherUserProfileActivity.FirebaseCallback {
            override fun onCallback(list: MutableList<Property>) {
                txtProperty.text=propertyIDList.size.toString()
                ref1=FirebaseDatabase.getInstance().getReference("Review")
                ref1.addValueEventListener(object :ValueEventListener{
                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
                            countRating=0
                            totalRating=0.0
                            for(h in snapshot.children){
                                for(i in propertyIDList){
                                    if(h.child("propertyID").getValue().toString().equals(i.propertyID)&&h.child("status").getValue().toString().equals("completed")){
                                        countRating+=1
                                        totalRating+=h.child("numStar").getValue().toString().toDouble()
                                    }
                                }
                            }
                            if(totalRating.equals(0.0)){
                                txtReview.text = "0.0"
                            }else {
                                txtReview.text =
                                    String.format("%.1f", totalRating / countRating)
                                personRating.setRating((totalRating / countRating).toFloat())
                            }
                        }
                    }

                })
            }
        })
    }

    private fun readData(firebaseCallback:FirebaseCallback){

        ref=FirebaseDatabase.getInstance().getReference("Property")
        ref.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    propertyIDList.clear()
                    for(h in snapshot.children){
                        if(h.child("userID").getValue().toString().equals(otherUserID)&&
                            h.child("status").getValue().toString().equals("available")){
                            val property=h.getValue(Property::class.java)
                            propertyIDList.add(property!!)
                        }
                    }
                    firebaseCallback.onCallback(propertyIDList)
                }
            }

        })
    }


    private interface FirebaseCallback{
        fun onCallback(list : MutableList<Property>)
    }

}