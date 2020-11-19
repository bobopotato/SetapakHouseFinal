package com.example.setapakhouse

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_search1.*


class SearchActivity1 : AppCompatActivity() {

    lateinit var epicDialog : Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search1)

        setSupportActionBar(toolbar as Toolbar?)
        supportActionBar?.setTitle("Search - Filter 1")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val renterType = intent.getStringExtra("RenterType")!!
        val propertyType = intent.getStringExtra("PropertyType")!!

        rentalTypeText.text = renterType
        propertyTypeText.text = propertyType

        epicDialog = Dialog(this)

        if (renterType == "Short-Term"){
            text3.text = "Price per day (RM)"
        }

        if (propertyType == "House"){
            isHouse()
        }
        if (propertyType == "Room"){
            isRoom()
        }

        val spinner1: Spinner = findViewById(R.id.minPrice)
        val spinner2: Spinner = findViewById(R.id.maxPrice)
        spinner1.prompt = "From Mix"
        spinner2.prompt = "to Max"
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            this,
            R.array.min_price,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner1.adapter = adapter
        }

        ArrayAdapter.createFromResource(
            this,
            R.array.max_price,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner2.adapter = adapter
        }
        var noFilter = true
        var filterPrice = false
        var onlyMin = false
        var onlyMax = false
        var validPrice = true
        var filterType = "default"

        searchBtn.setOnClickListener {
            noFilter = true
            filterPrice = false
            onlyMin = false
            onlyMax = false
            validPrice = true
            filterType = "default"

            var min = spinner1.selectedItem.toString().split(" ")
            var max = spinner2.selectedItem.toString().split(" ")
            var minPrice = min[1]
            var maxPrice = max[1]

            if(minPrice!="Min" && maxPrice!="Max"){
                onlyMin = false
                onlyMax = false
                noFilter=false
            }
            else{
                noFilter=true
                filterType = "default"
            }


            if(minPrice=="Min" && maxPrice!="Max"){
                onlyMin = true
                onlyMax = false
                filterType = "onlyMax"
            }

            if(minPrice!="Min" && maxPrice=="Max"){
                onlyMin = false
                onlyMax = true
                filterType = "onlyMin"
            }

            if(!onlyMin && !onlyMax && !noFilter){
                filterPrice = true
                if(minPrice.toDouble() > maxPrice.toDouble()){
                    validPrice = false
                    filterType = "invalidPrice"
                }else{
                    validPrice = true
                    filterType = "validPrice"
                }
            }

            //Check price filter
            if(filterType == "onlyMin"){
                showDialog("maximum")
                spinner2.setFocusable(true)
                spinner2.setFocusableInTouchMode(true)
                spinner2.requestFocus()
            }
            if(filterType == "onlyMax"){
                showDialog("minimum")
                spinner1.setFocusable(true)
                spinner1.setFocusableInTouchMode(true)
                spinner1.requestFocus()
            }
            if(filterType == "invalidPrice"){
                Log.d("abc", "price = " + minPrice)
                Log.d("abc", "price = " + maxPrice)

                showDialog("valid")
                spinner1.setFocusable(true)
                spinner1.setFocusableInTouchMode(true)
                spinner1.requestFocus()
                spinner2.setFocusable(true)
                spinner2.setFocusableInTouchMode(true)
                spinner2.requestFocus()
            }

            if(filterType !="onlyMin" && filterType !="onlyMax" && filterType != "invalidPrice"){
                if(propertyType == "House"){
                    var houseType = houseTypeSpinner.selectedItem.toString()

                    //next page
                    val intent = Intent(this, SearchActivity2::class.java)
                    intent.putExtra("PropertyName", searchBox.text.toString())
                    intent.putExtra("RenterType", renterType)
                    intent.putExtra("PropertyType", propertyType)
                    if(houseType != "House Type"){
                        intent.putExtra("GotChooseHouseType", "yes")
                        intent.putExtra("HouseType", houseType)
                    }else{
                        intent.putExtra("GotChooseHouseType", "no")
                    }
                    intent.putExtra("Preference", getPreference())
                    if(filterType=="default"){
                        intent.putExtra("filterType", "default")
                    }
                    if(filterType=="validPrice"){
                        intent.putExtra("filterType", "validPrice")
                        intent.putExtra("MinPrice", minPrice)
                        intent.putExtra("MaxPrice", maxPrice)
                    }
                    startActivity(intent)
                }

                if(propertyType == "Room"){
                    val roomType = houseTypeSpinner.selectedItem.toString()

                    //next page
                    val intent = Intent(this, SearchActivity2::class.java)
                    intent.putExtra("PropertyName", searchBox.text.toString())
                    intent.putExtra("RenterType", renterType)
                    intent.putExtra("PropertyType", propertyType)
                    if(roomType != "Room Type"){
                        intent.putExtra("GotChooseRoomType", "yes")
                        intent.putExtra("RoomType", roomType)
                    }else{
                        intent.putExtra("GotChooseRoomType", "no")
                    }
                    intent.putExtra("Preference", getPreference())
                    if(filterType=="default"){
                        intent.putExtra("filterType", "default")
                    }
                    if(filterType=="validPrice"){
                        intent.putExtra("filterType", "validPrice")
                        intent.putExtra("MinPrice", minPrice)
                        intent.putExtra("MaxPrice", maxPrice)
                    }
                    startActivity(intent)
                }
            }
        }

    }

    private fun isHouse(){
        val spinner1: Spinner = findViewById(R.id.houseTypeSpinner)
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            this,
            R.array.house_type_filter,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner1.adapter = adapter
        }

    }

    private fun isRoom(){
        text4.text = "Room Type"
        val spinner1: Spinner = findViewById(R.id.houseTypeSpinner)

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            this,
            R.array.room_type_filter,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner1.adapter = adapter
        }

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

        return preference
    }

    private fun showDialog(type : String){
        epicDialog.setContentView(R.layout.popup_error)
        //val closeButton : ImageView = epicDialog.findViewById(R.id.closeBtn)
        val okButton : Button = epicDialog.findViewById(R.id.okBtn)
        val title : TextView = epicDialog.findViewById(R.id.title)
        val content : TextView = epicDialog.findViewById(R.id.content)

        title.text = "Error"
        content.text = "Please choose " + type + " price"

        okButton.setOnClickListener {
            epicDialog.dismiss()
        }
        epicDialog.setCancelable(true)
        epicDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        epicDialog.show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == android.R.id.home){
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}