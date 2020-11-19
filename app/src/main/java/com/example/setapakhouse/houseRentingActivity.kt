package com.example.setapakhouse

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.setapakhouse.Adapter.fragmentAdapter3
import com.example.setapakhouse.Adapter.houseRentingAdapter
import com.example.setapakhouse.Adapter.outgoingAdapter
import com.example.setapakhouse.Adapter.rentingHouseFragmentAdapter
import com.example.setapakhouse.Model.Property
import com.example.setapakhouse.Model.Rent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_house_renting.*
import kotlinx.android.synthetic.main.activity_my_payment.*
import kotlinx.android.synthetic.main.fragment_outgoing.view.*

class houseRentingActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_house_renting)

        houseBack.bringToFront()

        houseBack.setOnClickListener {
            finish()
        }

        viewPagerRentingHouse.adapter= rentingHouseFragmentAdapter(supportFragmentManager)
        tabLayoutRentingHouse.setupWithViewPager(viewPagerRentingHouse)

    }
}