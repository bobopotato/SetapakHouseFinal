package com.example.setapakhouse.Model

class User (var userID:String, var username:String, var email:String, var image:String, var phoneNumber:String,var fullName:String,var rewardPoint:Int,var balance:Double) {
    constructor() : this("", "", "", "", "", "", 0, 0.0) {

    }

}