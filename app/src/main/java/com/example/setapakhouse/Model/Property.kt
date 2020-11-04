package com.example.setapakhouse.Model

class Property(val propertyID : String,val location : String,val price : Double, val postcode : Int, val accommodation:String,val preference:String,val propertyName:String,val rentalType:String,val status:String,val releaseDateTime:String,val userID:String,val description:String,val propertyType:String){

    constructor() : this("","",0.0,0,"","","","","","","","",""){

    }

}