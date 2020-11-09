package com.example.setapakhouse.Model

class Approval(val approvalID:String,val approvalContent:String,val approvalDateTime:String, val requestStartDate : String,val requestEndDate : String,val status:String,val userID:String,val propertyID:String, val notificationID : String) {
    constructor():this("","","","","","","","","")
}