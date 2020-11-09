package com.example.setapakhouse.Model

class Payment(val paymentID:String,val paymentTitle:String,val paymentDate:String,val paymentAmount:Double,val rewardPointUsed:Int,val status:String, val notificationID : String, val warningNotificationID : String, val rentID:String){

    constructor() : this("","","",0.00,0,"","","",""){

    }

}