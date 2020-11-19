package com.example.setapakhouse

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_search1.*
import kotlinx.android.synthetic.main.activity_search2.*
import kotlinx.android.synthetic.main.activity_search2.searchBtn
import kotlinx.android.synthetic.main.activity_search2.toolbar


class SearchActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search2)

        setSupportActionBar(toolbar as Toolbar?)
        supportActionBar?.setTitle("Search - Filter 2")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        expandableLayout.collapse()

        showMoreButton.setOnClickListener {
            expandableLayout.toggle()
            if (expandableLayout.isExpanded) {
                showMoreButton.setText("Show More  "+ getString(R.string.down))
            } else {
                showMoreButton.setText("Show Less  "+ getString(R.string.up))
            }
        }

        var renterType = intent.getStringExtra("RenterType")!!
        var propertyType = intent.getStringExtra("PropertyType")!!
        var filterType = intent.getStringExtra("filterType")!!
        var preference = intent.getStringExtra("Preference")!!


        //val gotChooseRoomType = intent.getStringExtra("GotChooseRoomType")!!

        //Log.d("abc", "zzz = " + preference)
        //Log.d("gotchoosemou", intent.getStringExtra("MaxPrice")!!)

        searchBtn.setOnClickListener {

            if(propertyType == "House"){
                val gotChooseHouseType = intent.getStringExtra("GotChooseHouseType")!!

                //next page
                val intent1 = Intent(this, SearchResultActivity::class.java)
                intent1.putExtra("PropertyName", intent.getStringExtra("PropertyName"))
                intent1.putExtra("RenterType", renterType)
                intent1.putExtra("PropertyType", propertyType)
                if(gotChooseHouseType == "yes"){
                    intent1.putExtra("GotChooseHouseType", "yes")
                    intent1.putExtra("HouseType", intent.getStringExtra("HouseType"))
                }else{
                    intent1.putExtra("GotChooseHouseType", "no")
                }
                intent1.putExtra("Preference", preference)
                intent1.putExtra("Accomodation",getAccomodation())
                if(filterType=="default"){
                    intent1.putExtra("filterType", "default")
                }
                if(filterType=="validPrice"){
                    intent1.putExtra("filterType", "validPrice")
                    intent1.putExtra("MinPrice", intent.getStringExtra("MinPrice")!!)
                    intent1.putExtra("MaxPrice", intent.getStringExtra("MaxPrice")!!)
                }
                startActivity(intent1)
            }
            if(propertyType == "Room"){
                var gotChooseHouseType = intent.getStringExtra("GotChooseRoomType")!!

                //next page
                val intent1 = Intent(this, SearchResultActivity::class.java)
                intent1.putExtra("PropertyName", intent.getStringExtra("PropertyName"))
                intent1.putExtra("RenterType", renterType)
                intent1.putExtra("PropertyType", propertyType)
                if(gotChooseHouseType == "yes"){
                    intent1.putExtra("GotChooseRoomType", "yes")
                    intent1.putExtra("RoomType", intent.getStringExtra("RoomType"))
                }else{
                    intent1.putExtra("GotChooseRoomType", "no")
                }
                intent1.putExtra("Preference", preference)
                intent1.putExtra("Accomodation",getAccomodation())
                if(filterType=="default"){
                    intent1.putExtra("filterType", "default")
                }
                if(filterType=="validPrice"){
                    intent1.putExtra("filterType", "validPrice")
                    intent1.putExtra("MinPrice", intent.getStringExtra("MinPrice")!!.toString())
                    intent1.putExtra("MaxPrice", intent.getStringExtra("MaxPrice")!!.toString())
                }
                startActivity(intent1)
            }

        }


    }

    private fun getAccomodation():String{
        var accomodation = ""
        var accomodationCount = 0

        if(showerCheck.isChecked) {
            if(accomodationCount > 0){
                accomodation += ","
            }
            accomodation += "Shower"
            accomodationCount++
        }
        if(privateBathCheck.isChecked) {
            if(accomodationCount > 0){
                accomodation += ","
            }
            accomodation += "Private Bathroom"
            accomodationCount++
        }
        if(shareBathCheck.isChecked) {
            if(accomodationCount > 0){
                accomodation += ","
            }
            accomodation += "Share Bathroom"
            accomodationCount++
        }
        if(tvCheck.isChecked) {
            if(accomodationCount > 0){
                accomodation += ","
            }
            accomodation += "Television"
            accomodationCount++
        }
        if(cookingCheck.isChecked) {
            if(accomodationCount > 0){
                accomodation += ","
            }
            accomodation += "Cooking Allowed"
            accomodationCount++
        }
        if(wifiCheck.isChecked) {
            if(accomodationCount > 0){
                accomodation += ","
            }
            accomodation += "Wifi"
            accomodationCount++
        }
        if(washingMachineCheck.isChecked) {
            if(accomodationCount > 0){
                accomodation += ","
            }
            accomodation += "Washing Machine"
            accomodationCount++
        }
        if(airconCheck.isChecked) {
            if(accomodationCount > 0){
                accomodation += ","
            }
            accomodation += "Air-Conditioning"
            accomodationCount++
        }
        if(miniMarketCheck.isChecked) {
            if(accomodationCount > 0){
                accomodation += ","
            }
            accomodation += "Mini Market"
            accomodationCount++
        }
        if(surauCheck.isChecked) {
            if(accomodationCount > 0){
                accomodation += ","
            }
            accomodation += "Surau"
            accomodationCount++
        }
        if(carParkCheck.isChecked) {
            if(accomodationCount > 0){
                accomodation += ","
            }
            accomodation += "Car Park"
            accomodationCount++
        }
        if(playgroundCheck.isChecked) {
            if(accomodationCount > 0){
                accomodation += ","
            }
            accomodation += "Play Ground"
            accomodationCount++
        }
        if(multiPurposeHallCheck.isChecked) {
            if(accomodationCount > 0){
                accomodation += ","
            }
            accomodation += "Multi-purpose Hall"
            accomodationCount++
        }
        if(okuFriendlyCheck.isChecked) {
            if(accomodationCount > 0){
                accomodation += ","
            }
            accomodation += "OKU Friendly"
            accomodationCount++
        }
        if(gymCheck.isChecked) {
            if(accomodationCount > 0){
                accomodation += ","
            }
            accomodation += "Gymnasium Facility"
            accomodationCount++
        }
        if(swimmingPoolCheck.isChecked) {
            if(accomodationCount > 0){
                accomodation += ","
            }
            accomodation += "Swimming Pools"
            accomodationCount++
        }
        if(securityGuardCheck.isChecked) {
            if(accomodationCount > 0){
                accomodation += ","
            }
            accomodation += "24-hour Security"
            accomodationCount++
        }
        if(busStopCheck.isChecked) {
            if(accomodationCount > 0){
                accomodation += ","
            }
            accomodation += "Near Bus Stop"
            accomodationCount++
        }
        if(lrtCheck.isChecked) {
            if(accomodationCount > 0){
                accomodation += ","
            }
            accomodation += "Near LRT"
            accomodationCount++
        }
        if(ktmCheck.isChecked) {
            if(accomodationCount > 0){
                accomodation += ","
            }
            accomodation += "Near KTM"
            accomodationCount++
        }
        if(mrtCheck.isChecked) {
            if(accomodationCount > 0){
                accomodation += ","
            }
            accomodation += "Near MRT"
            accomodationCount++
        }
        if(restaurantCheck.isChecked) {
            if(accomodationCount > 0){
                accomodation += ","
            }
            accomodation += "Near Restaurant"
            accomodationCount++
        }

        return accomodation
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == android.R.id.home){
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

}