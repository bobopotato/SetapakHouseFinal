package com.example.setapakhouse

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.Color.rgb
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.addTextChangedListener
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_edit_rental_house.*
import kotlinx.android.synthetic.main.activity_edit_rental_house.descriptionText
import kotlinx.android.synthetic.main.activity_edit_rental_house.propertyName
import kotlinx.android.synthetic.main.activity_edit_rental_house.propertyNameText
import kotlinx.android.synthetic.main.activity_edit_rental_house.rentalPrice
import kotlinx.android.synthetic.main.activity_edit_rental_house.rentalPriceText
import kotlinx.android.synthetic.main.activity_edit_rental_house.textCount
import kotlinx.android.synthetic.main.activity_edit_rental_house.toolbar
import kotlinx.android.synthetic.main.activity_post1.*
import kotlinx.android.synthetic.main.activity_post2.*

class EditRentalHouse : AppCompatActivity() {

    lateinit var ref : DatabaseReference
    lateinit var epicDialog : Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_rental_house)

        setSupportActionBar(toolbar as Toolbar?)
        supportActionBar?.setTitle("Edit Property")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        expandableLayout.collapse()

        epicDialog = Dialog(this)

        val propertyID = intent.getStringExtra("PropertyID")!!

        showMoreButton.setOnClickListener {
            expandableLayout.toggle()
            if (expandableLayout.isExpanded) {
                showMoreButton.setText("Show More  "+ getString(R.string.down))
            } else {
                showMoreButton.setText("Show Less  "+ getString(R.string.up))
            }
        }

        declareEverything()

        propertyNameText.addTextChangedListener {
            textCount.setText(propertyNameText.text.toString().length.toString() + "/30")
            validatePropertyName()

            propertyNameText.setTextColor(rgb(0,0,0))
        }

        rentalPriceText.addTextChangedListener {
            rentalPriceText.setTextColor(rgb(0,0,0))
        }

        descriptionText.addTextChangedListener {
            descriptionText.setTextColor(rgb(0,0,0))
        }


        submitChangeBtn.setOnClickListener {
            val accomodation = getAccomodation()
            val preference = getPreference()

            if(accomodation.isEmpty() || preference.isEmpty()){
                showDialog2()
            }
            else{
                //check validation
                if(validatePropertyName() && validateRentalPrice() && validateDescription()){

                    epicDialog.setContentView(R.layout.popup_confirmation)
                    val yesButton : Button = epicDialog.findViewById(R.id.yesBtn)
                    val cancelButton : Button = epicDialog.findViewById(R.id.cancelBtn)
                    val title : TextView = epicDialog.findViewById(R.id.title)
                    val content : TextView = epicDialog.findViewById(R.id.content)

                    title.text = "Edit Confirmation"
                    content.text = "Are you sure to edit your property detail?"
                    yesButton.text = "Yes"
                    yesButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)

                    yesButton.setOnClickListener {
                        val propertyName = propertyNameText.text.toString().trim()
                        val rentalPrice = rentalPriceText.text.toString().trim()
                        val description = descriptionText.text.toString().trim()
                        val accomodation = getAccomodation()
                        val preference = getPreference()

                        //submit changes
                        ref = FirebaseDatabase.getInstance().getReference("Property").child(propertyID)

                        ref.child("propertyName").setValue(propertyName)
                        ref.child("price").setValue(rentalPrice.toDouble())
                        ref.child("description").setValue(description)
                        ref.child("accommodation").setValue(accomodation)
                        ref.child("preference").setValue(preference)

                        showDialog(epicDialog)
                    }
                    cancelButton.setOnClickListener {
                        epicDialog.dismiss()
                    }
                    epicDialog.setCancelable(false)
                    epicDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    epicDialog.show()

                }
            }
        }

    }

    private fun showDialog2(){
        epicDialog.setContentView(R.layout.popup_error)
        //val closeButton : ImageView = epicDialog.findViewById(R.id.closeBtn)
        val okButton : Button = epicDialog.findViewById(R.id.okBtn)
        val title : TextView = epicDialog.findViewById(R.id.title)
        val content : TextView = epicDialog.findViewById(R.id.content)

        title.text = "Insufficient Information"
        content.text = "Please select more than 6 accommodations and 4 preferences."

        okButton.setOnClickListener {
            epicDialog.dismiss()
        }
        epicDialog.setCancelable(true)
        epicDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        epicDialog.show()

    }

    private fun showDialog(abc: Dialog){
        epicDialog.setContentView(R.layout.popup_positive)
        //val closeButton : ImageView = epicDialog.findViewById(R.id.closeBtn)
        val okButton : Button = epicDialog.findViewById(R.id.okBtn)
        val title : TextView = epicDialog.findViewById(R.id.title)
        val content : TextView = epicDialog.findViewById(R.id.content)

        title.text = "Edit Successful"
        content.text = "Your property has been edited successfully !"

        okButton.setOnClickListener {
            epicDialog.dismiss()
            abc.dismiss()
            finish()
        }
        epicDialog.setCancelable(false)
        epicDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        epicDialog.show()
    }

    private fun validatePropertyName():Boolean{
        var propertyName = propertyNameText.text.toString().trim()

        if(propertyName.length>30){
            propertyNameText.setError("                     Username too long !")
            propertyNameText.requestFocus()
            textCount.setTextColor(getColor(R.color.red))
            return false
        }else{
            textCount.setTextColor(getColor(R.color.black))
        }

        if(propertyName.isEmpty()){
            propertyNameText.setError("                     Field can't be empty !")
            propertyNameText.requestFocus()
            return false
        }else{
            propertyNameText.setError(null)
            return true
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

    private fun declareEverything(){
        //Get previous intent data
        val renterType = intent.getStringExtra("RenterType")!!
        val propertyName = intent.getStringExtra("PropertyName")!!
        val rentalPrice1 = intent.getStringExtra("RentalPrice")!!
        val description = intent.getStringExtra("Description")!!
        val accomodation = intent.getStringExtra("Accomodation")!!
        val preference = intent.getStringExtra("Preference")!!

        if (renterType == "Long-Term"){
            rentalPrice.text = "Monthly Rental Price (RM)"
        }
        if(renterType == "Short-Term"){
            rentalPrice.text = "Daily Rental Price (RM)"
        }

        propertyNameText.setText(propertyName)
        propertyNameText.setTextColor(rgb(38,153,251))

        rentalPriceText.setText(String.format("%.2f",rentalPrice1.toDouble()))
        rentalPriceText.setTextColor(rgb(38,153,251))

        descriptionText.setText(description)
        descriptionText.setTextColor(rgb(38,153,251))

        val accomodationArray = accomodation.split(",")
        val preferenceArray = preference.split(",")

        for(x in accomodationArray){
            if(x == "Shower"){
                showerCheck.isChecked=true
            }
            if(x == "Private Bathroom"){
                privateBathCheck.isChecked=true
            }
            if(x == "Share Bathroom"){
                shareBathCheck.isChecked=true
            }
            if(x == "Television"){
                tvCheck.isChecked=true
            }
            if(x == "Cooking Allowed"){
                cookingCheck.isChecked=true
            }
            if(x == "Wifi"){
                wifiCheck.isChecked=true
            }
            if(x == "Washing Machine"){
                washingMachineCheck.isChecked=true
            }
            if(x == "Air-Conditioning"){
                airconCheck.isChecked=true
            }
            if(x == "Mini Market"){
                miniMarketCheck.isChecked=true
            }
            if(x == "Surau"){
                surauCheck.isChecked=true
            }
            if(x == "Car Park"){
                carParkCheck.isChecked=true
            }
            if(x == "Play Ground"){
                playgroundCheck.isChecked=true
            }
            if(x == "Multi-purpose Hall"){
                multiPurposeHallCheck.isChecked=true
            }
            if(x == "OKU Friendly"){
                okuFriendlyCheck.isChecked=true
            }
            if(x == "Gymnasium Facility"){
                gymCheck.isChecked=true
            }
            if(x == "Swimming Pools"){
                swimmingPoolCheck.isChecked=true
            }
            if(x == "24-hour Security"){
                securityGuardCheck.isChecked=true
            }
            if(x == "Near Bus Stop"){
                busStopCheck.isChecked=true
            }
            if(x == "Near LRT"){
                lrtCheck.isChecked=true
            }
            if(x == "Near KTM"){
                ktmCheck.isChecked=true
            }
            if(x == "Near MRT"){
                mrtCheck.isChecked=true
            }
            if(x == "Near Restaurant"){
                restaurantCheck.isChecked=true
            }

        }

        for(x in preferenceArray){
            if(x == "Single Male"){
                singleMaleCheck.isChecked=true
            }
            if(x == "Single Female"){
                singleFemaleCheck.isChecked=true
            }
            if(x == "Couple"){
                coupleCheck.isChecked=true
            }
            if(x == "Family"){
                familyCheck.isChecked=true
            }
            if(x == "Malaysian"){
                malaysianCheck.isChecked=true
            }
            if(x == "Non-Malaysian"){
                nonMalaysianCheck.isChecked=true
            }
            if(x == "Malay"){
                malayCheck.isChecked=true
            }
            if(x == "Chinese"){
                chineseCheck.isChecked=true
            }
            if(x == "Indian"){
                indianCheck.isChecked=true
            }
            if(x == "Others"){
                otherCheck.isChecked=true
            }
            if(x == "Student"){
                studentCheck.isChecked=true
            }
            if(x == "Employed"){
                employedCheck.isChecked=true
            }
            if(x == "Unemployed"){
                unemployedCheck.isChecked=true
            }
            if(x == "Smoking Allowed"){
                smokingCheck.isChecked=true
            }
            if(x == "Pet Allowed"){
                petCheck.isChecked=true
            }
            if(x == "Muslim Friendly"){
                muslimCheck.isChecked=true
            }
            if(x == "Move-in immediately"){
                moveInCheck.isChecked=true
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == android.R.id.home){
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}