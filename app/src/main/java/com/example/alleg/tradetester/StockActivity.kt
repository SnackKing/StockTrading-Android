package com.example.alleg.tradetester

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData
import kotlinx.android.synthetic.main.activity_stock.*
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.utils.ViewPortHandler
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import java.text.DecimalFormat


class StockActivity : AppCompatActivity() {
    private lateinit var sym:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stock)
        val mJsonObject = JSONObject(intent.getStringExtra("data"))
        sym =  mJsonObject.get("symbol").toString()
        symbol.text = sym

        Log.d("STOCK", "THIS WORKED")
        APIUtils.HistoryResponse(sym,this)




    }
    fun setupLineChart(historyData:APIUtils.HistoryResponse){

        val entries = ArrayList<Entry>()
        for(i in 0 until 5){
            entries.add(Entry(i.toFloat(),historyData.prices[i]))
        }
        val linedata:LineDataSet = LineDataSet(entries, "Test Data")
        chart.setData(LineData(linedata))
        chart.xAxis.granularity = 1f
        chart.xAxis.setValueFormatter(MyValueFormatter(historyData.labels))
        chart.invalidate()
    }

    inner class MyValueFormatter(val dates:ArrayList<String>) : IAxisValueFormatter {


        private val mFormat: DecimalFormat

        init {
            mFormat = DecimalFormat("###,###,##0") // use one decimal
        }
        override fun getFormattedValue(value: Float, axis: AxisBase?): String {
            return dates[value.toInt()]
        }

    }
}
