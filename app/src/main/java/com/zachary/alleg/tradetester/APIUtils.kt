package com.zachary.alleg.tradetester

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.stock_content.*
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


object APIUtils{
    fun makeAPICallForSingleStock(symbol: String, context: Context) {
        val queue = Volley.newRequestQueue(context)
        var url = "https://www.worldtradingdata.com/api/v1/stock?"
        url += "api_token=" + getAPIKeyWTD()
        url += "&symbol=" + symbol
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
                Response.Listener { response ->
                    if (response.has("data")) {
                        var data: JSONArray = response.getJSONArray("data")
                        val stock: JSONObject = data.getJSONObject(0)
                        var stockIntent = Intent(context, StockActivity::class.java)
                        stockIntent.putExtra("data", stock.toString())
                        stockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                        context.startActivity(stockIntent)

                    } else {
                        var errorIntent = Intent(context, ErrorActivity::class.java)
                        errorIntent.putExtra("code", ErrorActivity.ErrorCodes.NO_STOCK_FOUND)
                        errorIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(errorIntent)
                    }

                },

                Response.ErrorListener { error ->
                    // TODO: Handle error
                }
        )
        val st = jsonObjectRequest.toString()
        queue.add(jsonObjectRequest)


    }

    fun makeNewsAPICall(symbol:String, context:Context){

        val queue = Volley.newRequestQueue(context)
        var url = "https://stocknewsapi.com/api/v1?"
        url += "token=" + getAPIKeyNews()
        url += "&tickers=" + symbol
        url += "&type=article"
        url += "&items=5"
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
                Response.Listener { response ->
                    if (response.has("data")) {
                        var data: JSONArray = response.getJSONArray("data")
                        val newsList = arrayListOf<NewsItem>()
                        for(i in 0 until data.length()){
                             newsList.add(NewsItem(data.getJSONObject(i)))
                        }
                        val activity = context as StockActivity
                        activity.setupNewsList(newsList)


                    } else {
                      //oh snap no data came through
                    }

                },

                Response.ErrorListener { error ->
                    print(error.message)
                    val message = error.message
                    val activity = context as StockActivity
                    activity.newsError.visibility = View.VISIBLE
                    activity.setupNewsList(arrayListOf())
                }
        )
        val st = jsonObjectRequest.toString()
        queue.add(jsonObjectRequest)


    }
    class HistoryResponse(symbol: String, context: Context){
        var labels = arrayListOf<String>()
         var prices = arrayListOf<Float>()
        var ready = false

        init{
            val queue = Volley.newRequestQueue(context)
            val format = SimpleDateFormat("yyyy-MM-dd")
            val today = format.format(Date())
            val weekAgo = getEarlierDate(7, format)
            var url = "https://www.worldtradingdata.com/api/v1/history?"
            url += "api_token=" + getAPIKeyWTD()
            url += "&symbol=" + symbol
            url += "&date_from=" + weekAgo
            url += "&date_to=" + today
            url += "&sort=oldest"
            url += "&output=json"
            val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
                    Response.Listener { response ->
                            var data = response.getJSONObject("history")
                            val keys = data.names()
                            for (i in 0 until keys.length()) {
                                val key = keys.getString(i)
                                labels.add(key)
                                prices.add(data.getJSONObject(key).getString("close").toFloat())
                            }
                            ready = true
                        val act = context as StockActivity
                        act.setupLineChart(this)


                    },

                    Response.ErrorListener { error ->
                        // TODO: Handle error
                        Log.d("UTIL", error.toString())
                    }
            )
            queue.add(jsonObjectRequest)

        }
        private fun getEarlierDate(numDays:Int, format:SimpleDateFormat):String{
            val cal = Calendar.getInstance()
            cal.time = Date()
            cal.add(Calendar.DATE, numDays*-1)
            val dateBefore = cal.time
            return format.format(dateBefore)
        }
    }
    fun getPricesForAllOwned(owned:String, context: Context){
        val queue = Volley.newRequestQueue(context)
        var url = "https://cloud.iexapis.com/stable/tops/last?"
        url += "token=sk_0a0d416a40b6401a87b46811783be7be"
        url += "&symbols=" + owned
        val jsonObjectRequest = JsonArrayRequest(Request.Method.GET, url, null,
                Response.Listener { response ->
                    val priceDict = hashMapOf<String,Double>()
                    for(i in 0 until response.length()){
                        val stock:JSONObject = response.getJSONObject(i)
                        priceDict[stock.getString("symbol")] = stock.getDouble("price")
                    }
                    val act = context as AccountActivity
                    act.populateAssets(priceDict)

                },

                Response.ErrorListener { error ->
                    print(error.message)
                    val message = error.message
                }
        )
        queue.add(jsonObjectRequest)

    }
    fun getAPIKeyWTD():String{
        val rand:Int = (0..1).shuffled().first()
        if(rand == 0){
            return "BUy6ADUbn2X5OpX1LjoPhNZoZ5V91i9ciuD9rWIY7uzN8nrqgFAeyt7qlabJ"

        }
        else{
            return "MAImKvqnS835Ss55CnO1PSaex6L3u31zptrgVDWrzco9VoGFbv6obMnhJUxt"

        }
    }
    fun getAPIKeyNews():String{
        val rand:Int = (0..1).shuffled().first()
        if(rand == 0){
            return "jceey2zg7nzze5vvib9mvuyvnllco19rv4iubci4"

        }
        else{
            return "bkssnaaeyqt2vwwccqzlxhebr7ebs4rgi7krjmpn"

        }
    }
}