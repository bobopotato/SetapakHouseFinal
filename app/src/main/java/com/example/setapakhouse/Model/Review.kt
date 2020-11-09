package com.example.setapakhouse.Model

class Review(val reviewID:String,val reviewContent:String,val reviewDateTime:String,val numStar:Double,val propertyID:String,val userID:String,val notificationID:String,val status:String) {
    constructor():this("","","",0.0,"","","","")
}