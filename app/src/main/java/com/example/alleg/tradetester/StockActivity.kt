package com.example.alleg.tradetester

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import org.json.JSONArray
import org.json.JSONObject
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.android.synthetic.main.activity_stock.*


class StockActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stock)
        val mJsonObject = JSONObject(intent.getStringExtra("data"))
        symbol.text = mJsonObject.get("symbol").toString()
        val entries = ArrayList<Entry>()
        for(i in 1..7){
            entries.add(Entry(i.toFloat(),Math.random().toFloat()))
        }
        val linedata:LineDataSet = LineDataSet(entries, "Test Data")
        chart.setData(LineData(linedata))


    }
}
