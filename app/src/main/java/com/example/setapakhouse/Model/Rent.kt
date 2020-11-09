package com.example.setapakhouse.Model

class Rent(val rentID:String,val checkInDate:String,val checkOutDate:String,val status:String,val propertyID:String,val userID:String){

    constructor() : this("","","","","",""){

    }

}