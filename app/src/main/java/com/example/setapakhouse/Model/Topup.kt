package com.example.setapakhouse.Model

class Topup(var topupID:String,var topupDateTime:String,var topupAmount:Double,var userID:String) {
    constructor():this("","",0.0,"")
}