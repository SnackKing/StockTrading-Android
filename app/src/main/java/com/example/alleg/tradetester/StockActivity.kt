package com.example.alleg.tradetester

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import org.json.JSONArray
import org.json.JSONObject

class StockActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stock)
        val mJsonObject = JSONArray(intent.getStringExtra("data"))

    }
}
