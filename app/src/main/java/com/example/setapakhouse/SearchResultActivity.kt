package com.example.setapakhouse

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.setapakhouse.Adapter.HomeAdapter
import com.example.setapakhouse.Model.Property
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_search_result.*

class SearchResultActivity : AppCompatActivity() {

    lateinit var ref : DatabaseReference
    lateinit var ref2 : DatabaseReference
    lateinit var propertyList1 : MutableList<String>
    lateinit var propertyList2 : MutableList<Property>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result)

        propertyList1 = mutableListOf()
        propertyList2 = mutableListOf()

        //val propertyName = intent.getStringExtra("PropertyName")
        val propertyName = intent.getStringExtra("PropertyName")!!
        val renterType = intent.getStringExtra("RenterType")!!
        val propertyType = intent.getStringExtra("PropertyType")!!
        val filterType = intent.getStringExtra("filterType")!!
        val preference = intent.getStringExtra("Preference")!!
        val accomodation = intent.getStringExtra("Accomodation")!!
        var minPrice = ""
        var maxPrice = ""


        if(propertyName==""){
            resultText.text = "Search Result"
            resultText1.visibility = View.GONE
        }
        else{
            resultText.text = "Search Result For"
            resultText1.text = propertyName
            resultText1.visibility = View.VISIBLE
        }

        val preferenceArray = preference.split(",")
        val accomodationArray = accomodation.split(",")

        //Log.d("nonArray", "testing = " + preference)
        //for (x in preferenceArray){
        //    Log.d("arrayTest", "testing = " + x)
        //}


        if(propertyType == "House"){

            ref2 = FirebaseDatabase.getInstance().getReference("House")
            ref2.addValueEventListener(object: ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        propertyList1.clear()
                        for (h in snapshot.children){
                            if(intent.getStringExtra("GotChooseHouseType")!! == "yes"){
                                if(h.child("houseType").getValue().toString().equals(intent.getStringExtra("HouseType"))) {
                                    propertyList1.add(h.child("propertyID").getValue().toString())
                                }
                            }
                            else{
                                propertyList1.add(h.child("propertyID").getValue().toString())
                                Log.d("ppp", "zzz = aaa")
                            }
                        }

                        ref = FirebaseDatabase.getInstance().getReference("Property")
                        ref.addValueEventListener(object: ValueEventListener {
                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }

                            override fun onDataChange(snapshot: DataSnapshot) {
                                if(snapshot.exists()){
                                    propertyList2.clear()
                                    for (h in snapshot.children){
                                        var preferenceValid = true
                                        var accomodationValid = true
                                        if(h.child("status").getValue().toString().equals("available")) {
                                            val property = h.getValue(Property::class.java)
                                            val propertyName1 = h.child("propertyName").getValue().toString().toLowerCase()
                                            val propertyName2 = propertyName.toLowerCase()
                                            val location1 = h.child("location").getValue().toString().toLowerCase()
                                            for(x in propertyList1) {
                                                if (property!!.propertyID.equals(x) && property.rentalType.equals(renterType) && (propertyName1.contains(propertyName2)|| location1.contains(propertyName2))) {
                                                    if(filterType=="validPrice") {
                                                        minPrice = intent.getStringExtra("MinPrice")
                                                        maxPrice = intent.getStringExtra("MaxPrice")

                                                        if(property!!.price >= minPrice.toDouble() && property!!.price <= maxPrice.toDouble()) {
                                                            for (x in preferenceArray){
                                                                if(property.preference.contains(x)){

                                                                    for(y in accomodationArray){
                                                                        if(property.accommodation.contains(y)){

                                                                        }
                                                                        else{
                                                                            accomodationValid=false
                                                                        }
                                                                    }
                                                                }else{
                                                                    preferenceValid=false
                                                                }
                                                            }
                                                            if(preferenceValid && accomodationValid){
                                                                propertyList2.add(property)
                                                            }
                                                        }
                                                    }
                                                    else{
                                                        for (x in preferenceArray){
                                                            if(property.preference.contains(x)){

                                                                for(y in accomodationArray){
                                                                    if(property.accommodation.contains(y)){

                                                                    }
                                                                    else{
                                                                        accomodationValid=false
                                                                    }
                                                                }
                                                            }else{
                                                                preferenceValid=false
                                                            }
                                                        }
                                                        if(preferenceValid && accomodationValid){
                                                            propertyList2.add(property)
                                                        }
                                                    }

                                                }
                                            }
                                        }
                                    }

                                    if(propertyList2.size == 0){
                                        noRecordFound.visibility = View.VISIBLE
                                    }
                                    else{
                                        noRecordFound.visibility = View.GONE
                                    }

                                    val mLayoutManager = LinearLayoutManager(this@SearchResultActivity)
                                    mLayoutManager.reverseLayout = true
                                    resultFound.text = propertyList2.size.toString() + " results founded"
                                    recycler_view.layoutManager = mLayoutManager
                                    recycler_view.scrollToPosition(propertyList2.size-1)
                                    recycler_view.adapter = HomeAdapter(propertyList2, true)

                                }
                            }

                        })
                    }
                }

            })





            /*if(gotChooseHouseType == "yes"){
                 intent.getStringExtra("HouseType")
            }

            if(filterType=="validPrice"){
                minPrice = intent.getStringExtra("MinPrice")!!
                maxPrice =  intent.getStringExtra("MaxPrice")!!
            }*/

        }


        if(propertyType == "Room"){
            ref2 = FirebaseDatabase.getInstance().getReference("Room")
            ref2.addValueEventListener(object: ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        propertyList1.clear()
                        for (h in snapshot.children){
                            if(intent.getStringExtra("GotChooseRoomType")!! == "yes"){
                                if(h.child("roomType").getValue().toString().equals(intent.getStringExtra("RoomType"))) {
                                    propertyList1.add(h.child("propertyID").getValue().toString())
                                }
                            }
                            else{
                                propertyList1.add(h.child("propertyID").getValue().toString())
                                Log.d("ppp", "zzz = aaa")
                            }
                        }

                        ref = FirebaseDatabase.getInstance().getReference("Property")
                        ref.addValueEventListener(object: ValueEventListener {
                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }

                            override fun onDataChange(snapshot: DataSnapshot) {
                                if(snapshot.exists()){
                                    propertyList2.clear()
                                    for (h in snapshot.children){
                                        var preferenceValid = true
                                        var accomodationValid = true
                                        if(h.child("status").getValue().toString().equals("available")) {
                                            val property = h.getValue(Property::class.java)
                                            val propertyName1 = h.child("propertyName").getValue().toString().toLowerCase()
                                            val propertyName2 = propertyName.toLowerCase()
                                            val location1 = h.child("location").getValue().toString().toLowerCase()
                                            for(x in propertyList1) {
                                                if (property!!.propertyID.equals(x) && property.rentalType.equals(renterType) && (propertyName1.contains(propertyName2)|| location1.contains(propertyName2))) {
                                                    if(filterType=="validPrice") {
                                                        minPrice = intent.getStringExtra("MinPrice")
                                                        maxPrice = intent.getStringExtra("MaxPrice")

                                                        if(property!!.price >= minPrice.toDouble() && property!!.price <= maxPrice.toDouble()) {
                                                            for (x in preferenceArray){
                                                                if(property.preference.contains(x)){

                                                                    for(y in accomodationArray){
                                                                        if(property.accommodation.contains(y)){

                                                                        }
                                                                        else{
                                                                            accomodationValid=false
                                                                        }
                                                                    }
                                                                }else{
                                                                    preferenceValid=false
                                                                }
                                                            }
                                                            if(preferenceValid && accomodationValid){
                                                                propertyList2.add(property)
                                                            }
                                                        }
                                                    }
                                                    else{
                                                        for (x in preferenceArray){
                                                            if(property.preference.contains(x)){

                                                                for(y in accomodationArray){
                                                                    if(property.accommodation.contains(y)){

                                                                    }
                                                                    else{
                                                                        accomodationValid=false
                                                                    }
                                                                }
                                                            }else{
                                                                preferenceValid=false
                                                            }
                                                        }
                                                        if(preferenceValid && accomodationValid){
                                                            propertyList2.add(property)
                                                        }
                                                    }

                                                }
                                            }
                                        }
                                    }
                                    val mLayoutManager = LinearLayoutManager(this@SearchResultActivity)
                                    mLayoutManager.reverseLayout = true
                                    resultFound.text = propertyList2.size.toString() + " results founded"
                                    recycler_view.layoutManager = mLayoutManager
                                    recycler_view.scrollToPosition(propertyList2.size-1)
                                    recycler_view.adapter = HomeAdapter(propertyList2, true)

                                }
                            }

                        })
                    }
                }

            })

        }



        /*ref = FirebaseDatabase.getInstance().getReference("Property")
        ref.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    propertyList.clear()
                    for (h in snapshot.children){
                        //val propertyName1 = h.child("propertyName").getValue().toString().toLowerCase()
                        //val propertyName2 = propertyName.toLowerCase()
                        val location1 = h.child("location").getValue().toString().toLowerCase()
                        //if(h.child("status").getValue().toString().equals("available") && (propertyName1.contains(propertyName2)|| location1.contains(propertyName2))) {
                        if(h.child("status").getValue().toString().equals("available")) {
                            val property = h.getValue(Property::class.java)
                            propertyList.add(property!!)
                            //Log.d(tag,propertyList.toString())
                        }

                    }

                    val mLayoutManager = LinearLayoutManager(this@SearchResultActivity)
                    mLayoutManager.reverseLayout = true
                    resultFound.text = propertyList.size.toString() + " results founded"
                    recycler_view.layoutManager = mLayoutManager
                    recycler_view.scrollToPosition(propertyList.size-1)
                    recycler_view.adapter = HomeAdapter(propertyList)
                }
            }

        })*/

        backBtn.setOnClickListener {
            finish()
        }



    }


}