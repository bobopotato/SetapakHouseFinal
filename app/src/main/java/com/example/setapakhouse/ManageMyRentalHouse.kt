package com.example.setapakhouse

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.setapakhouse.Adapter.HomeAdapter
import com.example.setapakhouse.Adapter.MyRentalHouseAdapter
import com.example.setapakhouse.Model.Property
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_manage_my_rental_house.*
import kotlinx.android.synthetic.main.activity_manage_my_rental_house.noRecordFound
import kotlinx.android.synthetic.main.activity_manage_my_rental_house.toolbar



class ManageMyRentalHouse : AppCompatActivity() {

    lateinit var ref : DatabaseReference
    lateinit var ref2 : DatabaseReference
    lateinit var propertyList : MutableList<Property>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_my_rental_house)

        setSupportActionBar(toolbar as Toolbar?)
        supportActionBar?.setTitle("My Rental House")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        propertyList = mutableListOf()

        val currentUserID= FirebaseAuth.getInstance().currentUser!!.uid

        ref = FirebaseDatabase.getInstance().getReference("Property")
        ref.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    propertyList.clear()
                    for (h in snapshot.children){
                        if(h.child("status").getValue().toString()!="unavailable" && h.child("userID").getValue().toString().equals(currentUserID)) {
                            //if(h.child("status").getValue().toString().equals("available")) {
                            val property = h.getValue(Property::class.java)
                            propertyList.add(property!!)
                            //Log.d(tag,propertyList.toString())
                        }

                    }

                    if(propertyList.size==0){
                        noRecordFound.visibility = View.VISIBLE
                    }
                    else{
                        noRecordFound.visibility = View.GONE
                    }

                    val mLayoutManager = LinearLayoutManager(this@ManageMyRentalHouse)
                    mLayoutManager.reverseLayout = true
                    resultFound.text = propertyList.size.toString() + " results founded"
                    recycler_view.layoutManager = mLayoutManager
                    recycler_view.scrollToPosition(propertyList.size-1)
                    recycler_view.adapter = MyRentalHouseAdapter(propertyList)
                }
            }
        })


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == android.R.id.home){
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

}