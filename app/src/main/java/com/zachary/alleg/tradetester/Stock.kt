package com.zachary.alleg.tradetester

import org.json.JSONObject

data class Stock(var symbol:String, var name:String = "", var price:Float, var change:Float,var change_pct:Float , var numOwned:Int = 0){
    var high:Float = 0.0f
    var low:Float = 0.0f
    var yearHigh:Float = 0.0f
    var yearLow:Float = 0.0f
    var market_cap:String = ""
    var shares:String = ""
    var volume:String = ""
    var volume_avg:String = ""
    var totalReturn = 0f
    var numTransactions = 0
    var curReturn  = 0f

    constructor(data: JSONObject): this(data.getString("symbol"), data.getString("name"), data.get("price").toString().toFloat(), 0f, 0f)
    {
        try{
            change = data.get("day_change").toString().toFloat()
            change_pct =data.get("change_pct").toString().toFloat()
        }
        catch(e:NumberFormatException){
            change = 0f
            change_pct = 0f
        }
        high = data.get("day_high").toString().toFloat()
        low = data.get("day_high").toString().toFloat()
        yearHigh = data.get("52_week_high").toString().toFloat()
        yearLow = data.get("52_week_low").toString().toFloat()
        market_cap = data.get("market_cap").toString()
        shares = data.get("shares").toString()
        volume = data.get("volume").toString()
        volume_avg = data.get("volume_avg").toString()

    }
}
