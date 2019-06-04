package com.example.alleg.tradetester

data class Stock(var symbol:String, var name:String = "", var price:Float, var change:Float, var numOwned:Int = 0)