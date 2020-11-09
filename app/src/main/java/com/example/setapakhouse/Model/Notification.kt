package com.example.setapakhouse.Model

class Notification(val notificationID:String,val sender:String,val status:String,val content:String,val notificationDateTime:String,val type:String,val userID:String) {
    constructor():this("","","","","","","")
}