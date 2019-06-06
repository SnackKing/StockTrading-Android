package com.example.alleg.tradetester

import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject
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
                        stockIntent.putExtra("data", data.toString())
                        stockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        context.startActivity(stockIntent)

                    } else {
                        var errorIntent = Intent(context, ErrorActivity::class.java)
                        errorIntent.putExtra("code", 1)
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
}