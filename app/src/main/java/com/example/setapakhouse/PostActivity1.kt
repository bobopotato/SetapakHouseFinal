package com.example.setapakhouse

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.MotionEvent
import android.view.animation.Animation
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import kotlinx.android.synthetic.main.activity_post1.*
import java.util.*


class PostActivity1 : AppCompatActivity() {

    lateinit var scaleUp: Animation
    lateinit var scaleDown: Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post1)

        setSupportActionBar(toolbar as Toolbar?)
        supportActionBar?.setTitle("Post - Step 1")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        scaleUp = android.view.animation.AnimationUtils.loadAnimation(this, R.anim.scale_up)
        scaleDown = android.view.animation.AnimationUtils.loadAnimation(this, R.anim.scale_down)

        var renterType = "Long-Term"
        var propertyType = "House"

        Places.initialize(this, "AIzaSyBAtADCWISs8VHLbeNRZq2Kk1jPBEGVpRg")

        locationText.setFocusable(false)

        locationText.setOnClickListener {
            var fieldList : List<Place.Field>  = Arrays.asList(
                Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME
            )

            val intent = Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY, fieldList
            ).build(this)

            startActivityForResult(intent, 100)

        }

        longTermButton.setOnTouchListener { v, event ->
            val action = event.action
            when(action){
                MotionEvent.ACTION_UP->{
                    longTermButton.startAnimation(scaleDown)

                }
                MotionEvent.ACTION_DOWN->{
                    longTermButton.startAnimation(scaleUp)
                    longTermButton.setBackgroundResource(R.drawable.round_button_blue)
                    shortTermButton.setBackgroundResource(R.drawable.round_button_white)
                    renterType="Long-Term"
                }
                else ->{

                }
            }

            true
        }

        shortTermButton.setOnTouchListener { v, event ->
            val action = event.action
            when(action){
                MotionEvent.ACTION_UP->{
                    shortTermButton.startAnimation(scaleDown)
                }
                MotionEvent.ACTION_DOWN->{
                    shortTermButton.startAnimation(scaleUp)
                    shortTermButton.setBackgroundResource(R.drawable.round_button_blue)
                    longTermButton.setBackgroundResource(R.drawable.round_button_white)
                    renterType="Short-Term"
                }
                else ->{

                }
            }

            true
        }

        houseButton.setOnTouchListener { v, event ->
            val action = event.action
            when(action){
                MotionEvent.ACTION_UP->{
                    houseButton.startAnimation(scaleDown)

                }
                MotionEvent.ACTION_DOWN->{
                    houseButton.startAnimation(scaleUp)
                    houseButton.setBackgroundResource(R.drawable.round_button_blue)
                    roomButton.setBackgroundResource(R.drawable.round_button_white)
                    propertyType = "House"
                }
                else ->{

                }
            }

            true
        }

        roomButton.setOnTouchListener { v, event ->
            val action = event.action
            when(action){
                MotionEvent.ACTION_UP->{
                    roomButton.startAnimation(scaleDown)
                }
                MotionEvent.ACTION_DOWN->{
                    roomButton.startAnimation(scaleUp)
                    roomButton.setBackgroundResource(R.drawable.round_button_blue)
                    houseButton.setBackgroundResource(R.drawable.round_button_white)
                    propertyType = "Room"
                }
                else ->{

                }
            }

            true
        }

        propertyNameText.addTextChangedListener {
            textCount.setText(propertyNameText.text.toString().length.toString() + "/30")
            validatePropertyName()
        }

        saveNextButton.setOnClickListener {
            if(validatePropertyName() && validateLocation() && validatePostCode()){
                val intent = Intent(this, PostActivity2::class.java)
                intent.putExtra("RenterType", renterType)
                intent.putExtra("PropertyType" , propertyType)
                intent.putExtra("PropertyName", propertyNameText.text.toString())
                intent.putExtra("Location", locationText.text.toString())
                intent.putExtra("PostCode", postCodeText.text.toString())
                startActivity(intent)
            }
        }
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

    private fun validateLocation():Boolean{
        var location = locationText.text.toString().trim()

        if(location.isEmpty()){
            locationText.setError("                     Field can't be empty !")
            locationText.requestFocus()
            return false
        }else{
            propertyNameText.setError(null)
            return true
        }
    }

    private fun validatePostCode():Boolean{
        val postCodeList = listOf(53000,53100,53200,53300,54000,54100,54200)
        var postCode = postCodeText.text.toString().trim()
        var validPostCode = false

        if(postCode.isEmpty()){
            postCodeText.setError("                     Field can't be empty !")
            postCodeText.requestFocus()
            return false

        }else{
            for(post in postCodeList){

                if(postCode==post.toString()){
                    validPostCode = true
                }
            }
            if(validPostCode){
                postCodeText.setError(null)
                return true
            }else{
                postCodeText.setError("                     Invalid Setapak post code!")
                postCodeText.requestFocus()
                return false
            }

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == android.R.id.home){
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //Toast.makeText(this@MainActivity, "FAIL 99", Toast.LENGTH_SHORT).show()

        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            //when success
            //initialize place
            val place : Place = Autocomplete.getPlaceFromIntent(data!!)
            //set address on editText
            locationText.setText(place.address)


        }
    }

}