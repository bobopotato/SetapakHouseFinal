package com.example.setapakhouse

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.renderscript.Script
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_post1.*
import kotlinx.android.synthetic.main.activity_post3.*
import kotlinx.android.synthetic.main.activity_post3.saveNextButton
import kotlinx.android.synthetic.main.activity_post3.toolbar


class PostActivity3 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post3)

        setSupportActionBar(toolbar as Toolbar?)
        supportActionBar?.setTitle("Post - Step 3")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        expandableLayout.collapse()

        //Get previous intent data
        val renterType = intent.getStringExtra("RenterType")!!
        val propertyType = intent.getStringExtra("PropertyType")!!
        val propertyName = intent.getStringExtra("PropertyName")!!
        val location = intent.getStringExtra("Location")!!
        val postCode = intent.getStringExtra("PostCode")!!
        val rentalPrice = intent.getStringExtra("RentalPrice")!!
        val description = intent.getStringExtra("Description")!!

        showMoreButton.setOnClickListener {
            expandableLayout.toggle()
            if (expandableLayout.isExpanded) {
                showMoreButton.setText("Show More  "+ getString(R.string.down))
            } else {
                showMoreButton.setText("Show Less  "+ getString(R.string.up))
            }
        }

        saveNextButton.setOnClickListener {
            val accomodation = getAccomodation()
            val preference = getPreference()

            if(accomodation.isEmpty() || preference.isEmpty()){
                //Log.d("fail", "Cannot empty lah sohai")
                val builder = AlertDialog.Builder(this@PostActivity3)
                builder.setTitle("Please follow the form requirement")
                builder.setMessage("Select more than 6 accomodations\nSelect more than 4 preferences")

                builder.setNeutralButton("Okay", { dialog: DialogInterface?, which: Int ->

                })
                builder.setCancelable(false)
                builder.show()
            }
            else{
                if(propertyType == "House"){
                    val houseType = intent.getStringExtra("HouseType")!!
                    val roomNo = intent.getStringExtra("RoomNo")!!
                    val bathRoomNo = intent.getStringExtra("BathRoomNo")!!

                    //next page
                    val intent = Intent(this, PostActivity4::class.java)
                    intent.putExtra("RenterType", renterType)
                    intent.putExtra("PropertyType" , propertyType)
                    intent.putExtra("PropertyName", propertyName)
                    intent.putExtra("Location", location)
                    intent.putExtra("PostCode", postCode)
                    intent.putExtra("RentalPrice", rentalPrice)
                    intent.putExtra("HouseType", houseType)
                    intent.putExtra("RoomNo", roomNo)
                    intent.putExtra("BathRoomNo", bathRoomNo)
                    intent.putExtra("Description", description)
                    intent.putExtra("Accomodation", accomodation)
                    intent.putExtra("Preference", preference)
                    startActivity(intent)
                }
                if(propertyType == "Room"){
                    val roomType = intent.getStringExtra("RoomType")!!
                    val maxOccupancy = intent.getStringExtra("MaxOccupancy")!!

                    //next page
                    val intent = Intent(this, PostActivity4::class.java)
                    intent.putExtra("RenterType", renterType)
                    intent.putExtra("PropertyType" , propertyType)
                    intent.putExtra("PropertyName", propertyName)
                    intent.putExtra("Location", location)
                    intent.putExtra("PostCode", postCode)
                    intent.putExtra("RentalPrice", rentalPrice)
                    intent.putExtra("RoomType", roomType)
                    intent.putExtra("MaxOccupancy", maxOccupancy)
                    intent.putExtra("Description", description)
                    intent.putExtra("Accomodation", accomodation)
                    intent.putExtra("Preference", preference)
                    startActivity(intent)
                }
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

        if(accomodationCount<6){
            accomodation = ""
        }

        return accomodation
    }

    private fun getPreference():String{
        var preference = ""
        var preferenceCount = 0

        if(singleMaleCheck.isChecked){
            if(preferenceCount>0){
                preference += ","
            }
            preference += "Single Male"
            preferenceCount++;
        }
        if(singleFemaleCheck.isChecked){
            if(preferenceCount>0){
                preference += ","
            }
            preference += "Single Female"
            preferenceCount++;
        }
        if(coupleCheck.isChecked){
            if(preferenceCount>0){
                preference += ","
            }
            preference += "Couple"
            preferenceCount++;
        }
        if(familyCheck.isChecked){
            if(preferenceCount>0){
                preference += ","
            }
            preference += "Family"
            preferenceCount++;
        }
        if(malaysianCheck.isChecked){
            if(preferenceCount>0){
                preference += ","
            }
            preference += "Malaysian"
            preferenceCount++;
        }
        if(nonMalaysianCheck.isChecked){
            if(preferenceCount>0){
                preference += ","
            }
            preference += "Non-Malaysian"
            preferenceCount++;
        }
        if(malayCheck.isChecked){
            if(preferenceCount>0){
                preference += ","
            }
            preference += "Malay"
            preferenceCount++;
        }
        if(chineseCheck.isChecked){
            if(preferenceCount>0){
                preference += ","
            }
            preference += "Chinese"
            preferenceCount++;
        }
        if(indianCheck.isChecked){
            if(preferenceCount>0){
                preference += ","
            }
            preference += "Indian"
            preferenceCount++;
        }
        if(otherCheck.isChecked){
            if(preferenceCount>0){
                preference += ","
            }
            preference += "Others"
            preferenceCount++;
        }
        if(studentCheck.isChecked){
            if(preferenceCount>0){
                preference += ","
            }
            preference += "Student"
            preferenceCount++;
        }
        if(employedCheck.isChecked){
            if(preferenceCount>0){
                preference += ","
            }
            preference += "Employed"
            preferenceCount++;
        }
        if(unemployedCheck.isChecked){
            if(preferenceCount>0){
                preference += ","
            }
            preference += "Unemployed"
            preferenceCount++;
        }
        if(smokingCheck.isChecked){
            if(preferenceCount>0){
                preference += ","
            }
            preference += "Smoking Allowed"
            preferenceCount++;
        }
        if(petCheck.isChecked){
            if(preferenceCount>0){
                preference += ","
            }
            preference += "Pet Allowed"
            preferenceCount++;
        }
        if(muslimCheck.isChecked){
            if(preferenceCount>0){
                preference += ","
            }
            preference += "Muslim Friendly"
            preferenceCount++;
        }
        if(moveInCheck.isChecked){
            if(preferenceCount>0){
                preference += ","
            }
            preference += "Move-in immediately"
            preferenceCount++;
        }

        if(preferenceCount < 4){
            preference = ""
        }

        return preference
    }




}

