package com.example.alleg.tradetester

import android.content.Context
import android.content.Intent
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


object APIUtils{
    public fun makeAPICallForSingleStock(symbol: String, context: Context) {
        val queue = Volley.newRequestQueue(context)
        var url = "https://www.worldtradingdata.com/api/v1/stock?"
        url += "api_token=mkUwgwc7TADeShHuZO7D2RRbeLu1b9PNd6Ptey0LkIeRliCUjdLJJB9UE4UX"
        url += "&symbol=" + symbol
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
                Response.Listener { response ->
                    if (response.has("data")) {
                        var data: JSONArray = response.getJSONArray("data")
                        val stock: JSONObject = data.getJSONObject(0)
                        var stockIntent = Intent(context, StockActivity::class.java)
                        stockIntent.putExtra("data", stock.toString())
                        stockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        context.startActivity(stockIntent)

                    } else {
                        var errorIntent = Intent(context, ErrorActivity::class.java)
                        errorIntent.putExtra("code", ErrorActivity.ErrorCodes.NO_STOCK_FOUND)
                        errorIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
        url += "token=ljuasvceqjjkuazku0elqdw2s3sbhqvhfkthvg6c"
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
                }
        )
        val st = jsonObjectRequest.toString()
        queue.add(jsonObjectRequest)


    }
    class HistoryResponse(symbol: String, context: Context){
        var labels = arrayListOf<String>()
         var prices = arrayListOf<Float>()
        var ready = false;
        init{
            val queue = Volley.newRequestQueue(context)
            val format = SimpleDateFormat("yyyy-MM-dd")
            val today = format.format(Date())
            val weekAgo = getEarlierDate(7, format)
            var url = "https://www.worldtradingdata.com/api/v1/history?"
            url += "api_token=mkUwgwc7TADeShHuZO7D2RRbeLu1b9PNd6Ptey0LkIeRliCUjdLJJB9UE4UX"
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
                            ready = true;
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
}