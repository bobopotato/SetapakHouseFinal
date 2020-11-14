package com.example.setapakhouse

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.setapakhouse.Adapter.houseRentingAdapter
import com.example.setapakhouse.Adapter.outgoingAdapter
import com.example.setapakhouse.Model.Property
import com.example.setapakhouse.Model.Rent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_house_renting.*
import kotlinx.android.synthetic.main.fragment_outgoing.view.*

class houseRentingActivity : AppCompatActivity() {

    lateinit var rentList:MutableList<Rent>
    lateinit var propertyList:MutableList<Property>
    lateinit var checkInList:MutableList<String>
    lateinit var checkOutList:MutableList<String>
    lateinit var ref: DatabaseReference
    lateinit var ref1: DatabaseReference
    val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_house_renting)

        rentList= mutableListOf()
        propertyList= mutableListOf()
        checkInList= mutableListOf()
        checkOutList= mutableListOf()
        houseBack.setOnClickListener {
            finish()
        }

        ref=FirebaseDatabase.getInstance().getReference("Rent")
        ref.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    rentList.clear()
                    for(h in snapshot.children){
                        if(h.child("status").getValue().toString().equals("continuing")&&
                            h.child("userID").getValue().toString().equals(currentUserID)){
                            val rent=h.getValue(Rent::class.java)
                            rentList.add(rent!!)
                        }
                    }
                    ref1=FirebaseDatabase.getInstance().getReference("Property")
                    ref1.addValueEventListener(object :ValueEventListener{
                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                        override fun onDataChange(snapshot: DataSnapshot) {
                            if(snapshot.exists()){
                                checkInList.clear()
                                checkOutList.clear()
                                propertyList.clear()
                                for(r in rentList){
                                    for(p in snapshot.children){

                                        if(p.child("propertyID").getValue().toString().equals(r.propertyID)){
                                            val property=p.getValue(Property::class.java)
                                            propertyList.add(property!!)
                                            checkOutList.add(r.checkOutDate)
                                            checkInList.add(r.checkInDate)
                                        }
                                    }
                                }
                                val mLayoutManager = LinearLayoutManager(this@houseRentingActivity)

                                houseRentingRecycle.layoutManager = mLayoutManager
                                houseRentingRecycle.adapter = houseRentingAdapter(propertyList,checkInList,checkOutList)


                            }
                        }

                    })


                }
            }

        })

    }
}