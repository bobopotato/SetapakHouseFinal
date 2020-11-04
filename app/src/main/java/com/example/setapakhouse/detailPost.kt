package com.example.setapakhouse

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.models.SlideModel
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.example.setapakhouse.Adapter.HomeAdapter
import com.example.setapakhouse.Adapter.accomAdapter
import com.example.setapakhouse.Adapter.fragmentAdapter
import com.example.setapakhouse.Adapter.reviewAdapter
import com.example.setapakhouse.Fragment.AccomFragment
import com.example.setapakhouse.Model.Property
import com.example.setapakhouse.Model.Review
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.ms.square.android.expandabletextview.ExpandableTextView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_detail_post.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.toolbar
import kotlinx.android.synthetic.main.fragment_accom.*
import kotlinx.android.synthetic.main.fragment_home.*

class detailPost : AppCompatActivity() {
    lateinit var ref: DatabaseReference
    lateinit var ref1: DatabaseReference
    lateinit var ref2: DatabaseReference
    lateinit var query: Query
    lateinit var reviewList : MutableList<Review>
    val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_post)
        setSupportActionBar(toolbar as Toolbar?)
        supportActionBar?.setTitle("Property Details")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //val slideModels:List<SlideModel>

        reviewList=mutableListOf()

        backButton.bringToFront()

        backButton.setOnClickListener{
            finish()
        }
        val slideModels=ArrayList<SlideModel>()

        val selectedPropertyID=intent.getStringExtra("selectedPosition")
        val selectedUserID=intent.getStringExtra("selectedUserID")

        if(selectedUserID.equals(currentUserID)){
            chatBtn.isEnabled=false
            chatBtn.isClickable=false
            chatBtn.setBackgroundResource(R.drawable.round_button_grey)
            chatBtn.visibility = View.INVISIBLE
            requestBtn.isEnabled=false
            requestBtn.isClickable=false
            requestBtn.setBackgroundResource(R.drawable.round_button_grey)
            requestBtn.visibility = View.INVISIBLE
        }

        chatBtn.setOnClickListener {

            val intent = Intent(this, MessageActivity::class.java)
            intent.putExtra("selectedUserID",selectedUserID)

            this.startActivity(intent)
        }
        val pref= PreferenceManager.getDefaultSharedPreferences(this)
        val editor=pref.edit()
        editor
            .putString("PROPERTYID",selectedPropertyID)
            .apply()

        val imageSlider = findViewById<ImageSlider>(R.id.imgProperty)

        ref=FirebaseDatabase.getInstance().getReference("Property")
        ref.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(h in snapshot.children){
                        if(h.child("propertyID").getValue().toString().equals(selectedPropertyID)){
                            if(h.child("rentalType").getValue().toString().equals("long")){
                                txtPrice.text="RM"+h.child("price").getValue().toString()+"/MONTH"
                            }else{
                                txtPrice.text="RM"+h.child("price").getValue().toString()+"/DAY"
                            }

                            txtPropertyType.text="Property Type: "+h.child("propertyType").getValue().toString()
                            txtRentalType.text="Rental Type: "+h.child("rentalType").getValue().toString()+" term"
                            txtPropertyName.text="Property Name: "+h.child("propertyName").getValue().toString()
                            txtLocation.text=h.child("location").getValue().toString()

                            if(h.child("propertyType").getValue().toString().equals("Room")){
                                ref1=FirebaseDatabase.getInstance().getReference("Room")
                                ref1.addValueEventListener(object:ValueEventListener{
                                    override fun onCancelled(error: DatabaseError) {
                                        TODO("Not yet implemented")
                                    }

                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        if(snapshot.exists()){
                                            for(h in snapshot.children){
                                                if(h.child("propertyID").getValue().toString().equals(selectedPropertyID)){
                                                    txtDetail.text="Room Type : "+h.child("roomType").getValue().toString()+"\nMaximum Occupancy : "+h.child("capacity").getValue().toString()
                                                }
                                            }
                                        }
                                    }

                                })
                            }else{
                                ref1=FirebaseDatabase.getInstance().getReference("House")
                                ref1.addValueEventListener(object:ValueEventListener{
                                    override fun onCancelled(error: DatabaseError) {
                                        TODO("Not yet implemented")
                                    }

                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        if(snapshot.exists()){
                                            for(h in snapshot.children){
                                                if(h.child("propertyID").getValue().toString().equals(selectedPropertyID)){
                                                    txtDetail.text="House Type : "+h.child("houseType").getValue().toString()+"\nNumber of Rooms : "+h.child("numRoom").getValue().toString()+"\nNumber of Bathrooms : "+h.child("numBathroom").getValue().toString()
                                                }
                                            }
                                        }
                                    }

                                })
                            }

                            val txtView:ExpandableTextView=findViewById(R.id.expand_tex_view)
                            txtView.text=txtView.text.toString()+(h.child("description").getValue().toString())

                            val propertyUID=h.child("userID").getValue().toString()
                            ref2=FirebaseDatabase.getInstance().getReference("Users")
                            ref2.addValueEventListener(object:ValueEventListener{
                                override fun onCancelled(error: DatabaseError) {
                                    TODO("Not yet implemented")
                                }

                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if(snapshot.exists()){
                                        for(h in snapshot.children){
                                            if(h.child("userID").getValue().toString().equals(propertyUID)){
                                                contactPhone.text=h.child("phoneNumber").getValue().toString()
                                                contactUsername.text=h.child("username").getValue().toString()
                                                Picasso.get().load(h.child("image").getValue().toString()).into(contactProfile)
                                            }
                                        }
                                    }
                                }

                            })
                        }
                    }
                }
            }

        })


        ref=FirebaseDatabase.getInstance().getReference("Review")
        ref.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    var total:Double=0.0
                    reviewList.clear()
                    for(h in snapshot.children){
                        if(h.child("propertyID").getValue().toString().equals(selectedPropertyID)){
                            val review = h.getValue(Review::class.java)
                            reviewList.add(review!!)

                            total+=h.child("numStar").getValue().toString().toDouble()
                        }
                    }
                    if(total.equals(0.0)){
                        averageRating.setRating(0.0f)
                        averageRating1.setRating(0.0f)
                        txtAverage.text="0.0"
                        txtAverage1.text="0.0"
                    }else{
                        val average:Double=String.format("%.1f", total/reviewList.size).toDouble()
                        averageRating.setRating(average.toFloat())
                        averageRating1.setRating(average.toFloat())
                        txtAverage.text=average.toString()
                        txtAverage1.text=average.toString()
                    }

                    val mLayoutManager = LinearLayoutManager(this@detailPost)
                    txtTotalRating.text=reviewList.size.toString()+" Ratings |"
                    reviewRecycle.layoutManager = mLayoutManager
                    reviewRecycle.adapter = reviewAdapter(reviewList)
                }
            }

        })


        query= FirebaseDatabase.getInstance().getReference("PropertyImage").orderByChild("imageName")
        query.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    for (h in snapshot.children) {
                        if(h.child("propertyID").getValue().toString().equals(selectedPropertyID)){
                            slideModels.add(SlideModel(h.child("imageSource").getValue().toString()))
                        }
                        imageSlider.setImageList(slideModels,ScaleTypes.CENTER_CROP)

                    }
                }
            }

        })


        detailViewPager.adapter= fragmentAdapter(supportFragmentManager)
        detailsTabLayout.setupWithViewPager(detailViewPager)

    }

}