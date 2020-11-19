package com.example.setapakhouse.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.setapakhouse.Adapter.houseRentingAdapter
import com.example.setapakhouse.Model.Property
import com.example.setapakhouse.Model.Rent
import com.example.setapakhouse.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_house_renting.*
import kotlinx.android.synthetic.main.fragment_house_renting.noRecordFound
import kotlinx.android.synthetic.main.fragment_house_renting.view.*


class HouseRentingFragment : Fragment() {

    lateinit var rentList:MutableList<Rent>
    lateinit var propertyList:MutableList<Property>
    lateinit var checkInList:MutableList<String>
    lateinit var checkOutList:MutableList<String>
    lateinit var rentingStatusList:MutableList<String>
    lateinit var ref: DatabaseReference
    lateinit var ref1: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root:View= inflater.inflate(R.layout.fragment_house_renting, container, false)
        val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
        rentingStatusList= mutableListOf()
        rentList= mutableListOf()
        propertyList= mutableListOf()
        checkInList= mutableListOf()
        checkOutList= mutableListOf()

        ref= FirebaseDatabase.getInstance().getReference("Rent")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    rentList.clear()
                    for(h in snapshot.children){
                        if(!(h.child("status").getValue().toString().equals("withdraw"))&&
                            !(h.child("status").getValue().toString().equals("completed"))&&
                            h.child("userID").getValue().toString().equals(currentUserID)){
                            val rent=h.getValue(Rent::class.java)
                            rentList.add(rent!!)
                        }
                    }
                    ref1= FirebaseDatabase.getInstance().getReference("Property")
                    ref1.addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                        override fun onDataChange(snapshot: DataSnapshot) {
                            if(snapshot.exists()){
                                checkInList.clear()
                                checkOutList.clear()
                                propertyList.clear()
                                rentingStatusList.clear()
                                for(r in rentList){
                                    for(p in snapshot.children){

                                        if(p.child("propertyID").getValue().toString().equals(r.propertyID)){
                                            val property=p.getValue(Property::class.java)
                                            propertyList.add(property!!)
                                            rentingStatusList.add(r.status)
                                            checkOutList.add(r.checkOutDate)
                                            checkInList.add(r.checkInDate)
                                        }
                                    }
                                }

                                if(propertyList.size==0){
                                    root.noRecordFound.visibility = View.VISIBLE
                                }
                                else{
                                    root.noRecordFound.visibility = View.GONE
                                }

                                val mLayoutManager = LinearLayoutManager(context)
                                mLayoutManager.reverseLayout=true
                                root.houseRentingRecycle.layoutManager = mLayoutManager
                                root.houseRentingRecycle.adapter = houseRentingAdapter(propertyList,checkInList,checkOutList,rentingStatusList)


                            }
                        }

                    })


                }
            }

        })
        return root
    }


}