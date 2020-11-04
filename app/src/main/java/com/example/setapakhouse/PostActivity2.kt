package com.example.setapakhouse

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_post1.*
import kotlinx.android.synthetic.main.activity_post2.*
import kotlinx.android.synthetic.main.activity_post2.saveNextButton
import kotlinx.android.synthetic.main.activity_post2.toolbar


class PostActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post2)

        setSupportActionBar(toolbar as Toolbar?)
        supportActionBar?.setTitle("Post - Step 2")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val renterType = intent.getStringExtra("RenterType")!!
        val propertyType = intent.getStringExtra("PropertyType")!!
        val propertyName = intent.getStringExtra("PropertyName")!!
        val location = intent.getStringExtra("Location")!!
        val postCode = intent.getStringExtra("PostCode")!!

        if(propertyType == "House"){
            isHouse()
        }

        if(propertyType == "Room"){
            isRoom()
            rentalPrice.setText("Daily Rental Price (RM)")
        }


        saveNextButton.setOnClickListener {
            //Toast.makeText(this, renterType + propertyType + propertyName + location + postCode, Toast.LENGTH_SHORT).show()
            if(validateRentalPrice() && validateDescription()){
                if(propertyType == "House"){
                    val rentalPrice = rentalPriceText.text.toString()
                    val houseType = houseTypeSpinner.getSelectedItem().toString()
                    val roomNo = roomNoSpinner.getSelectedItem().toString()
                    val bathRoomNo = bathroomNoSpinner.getSelectedItem().toString()
                    val description = descriptionText.text.toString()

                    //next page
                    val intent = Intent(this, PostActivity3::class.java)
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
                    startActivity(intent)

                }

                if(propertyType == "Room"){
                    val rentalPrice = rentalPriceText.text.toString()
                    val roomType = roomTypeSpinner.getSelectedItem().toString()
                    val maxOccupancy = maxOccupancySpinner.getSelectedItem().toString()
                    val description = descriptionText.text.toString()
                    roomTypeSpinner.getSelectedItem().toString()
                    maxOccupancySpinner.getSelectedItem().toString()

                    //next page
                    val intent = Intent(this, PostActivity3::class.java)
                    intent.putExtra("RenterType", renterType)
                    intent.putExtra("PropertyType" , propertyType)
                    intent.putExtra("PropertyName", propertyName)
                    intent.putExtra("Location", location)
                    intent.putExtra("PostCode", postCode)
                    intent.putExtra("RentalPrice", rentalPrice)
                    intent.putExtra("RoomType", roomType)
                    intent.putExtra("MaxOccupancy", maxOccupancy)
                    intent.putExtra("Description", description)
                    startActivity(intent)
                }
            }

        }
    }

    private fun validateRentalPrice():Boolean{
        var rentalPrice = rentalPriceText.text.toString().trim()

        if(rentalPrice.isEmpty()){
            rentalPriceText.setError("                     Field can't be empty !")
            rentalPriceText.requestFocus()
            return false
        }else{
            rentalPriceText.setError(null)
            return true
        }
    }

    private fun validateDescription():Boolean{
        var description = descriptionText.text.toString().trim()

        if(description.isEmpty()){
            descriptionText.setError("                     Field can't be empty !")
            descriptionText.requestFocus()
            return false
        }else{
            descriptionText.setError(null)
            return true
        }
    }




    private fun isHouse(){
        val spinner1: Spinner = findViewById(R.id.houseTypeSpinner)
        val spinner2: Spinner = findViewById(R.id.roomNoSpinner)
        val spinner3: Spinner = findViewById(R.id.bathroomNoSpinner)
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            this,
            R.array.house_type,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner1.adapter = adapter
        }

        ArrayAdapter.createFromResource(
            this,
            R.array.number,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner2.adapter = adapter
            spinner3.adapter = adapter
        }

    }

    private fun isRoom(){

        houseContainer.visibility =  View.GONE
        roomContainer.visibility = View.VISIBLE

        val spinner1: Spinner = findViewById(R.id.roomTypeSpinner)
        val spinner2: Spinner = findViewById(R.id.maxOccupancySpinner)

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            this,
            R.array.room_type,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner1.adapter = adapter
        }

        ArrayAdapter.createFromResource(
            this,
            R.array.number,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner2.adapter = adapter
        }

    }

}