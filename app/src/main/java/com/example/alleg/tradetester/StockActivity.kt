package com.example.alleg.tradetester

import android.nfc.Tag
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData
import kotlinx.android.synthetic.main.activity_stock.*
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.utils.ViewPortHandler
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.DecimalFormat


class StockActivity : AppCompatActivity() {
    private lateinit var sym:String
    private lateinit var stock:Stock
    lateinit var auth: FirebaseAuth
    lateinit var database: DatabaseReference
    val TAG = "STOCKPAGE"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stock)
        val mJsonObject = JSONObject(intent.getStringExtra("data"))
        stock = Stock(mJsonObject)
        sym = stock.symbol
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        setTextViews()

        Log.d("STOCK", "THIS WORKED")
        APIUtils.HistoryResponse(sym,this)




    }
    fun setTextViews(){
        symbol.text = stock.name + " (" + stock.symbol + ")"
        price.text = stock.price.toString() + " USD"
        change.text = "Today's Change: " +  stock.change.toString() + "USD"
        change_pct.text = "Change in %: " + stock.change_pct.toString() + "USD"
        todayHigh.text = "Today's High: " + stock.high
        todayLow.text = "Today's Low: " + stock.low
        yearHigh.text = "52 Week High: " + stock.yearHigh
        yearLow.text = "52 Week Low: " + stock.yearLow
        marketCap.text = "Market Cap: " + stock.market_cap
        shares.text = "Shares: " + stock.shares
        volume.text = "Volume: " + stock.volume
        volume_avg.text = "Volume Average: " + stock.volume_avg

        getUserStockData()

    }

    fun getUserStockData(){
        val uid = auth.uid
        val userRef = database.child("users").child(uid)
        val userListener = object : ValueEventListener {
            override fun onDataChange(user: DataSnapshot) {
                if(user.hasChild("owned") && user.child("owned").hasChild(sym)){
                    stock.numOwned =  (user.child("owned").child(sym).value as String).toInt()
                    numShares.text = "Shares Owned: "  + stock.numOwned
                    val equityVal = (stock.numOwned * stock.price)
                    equity.text = "Total Equity: " + equityVal
                    if (stock.numOwned != 0)currentReturn.text = "Return: " + (user.child("return").child(sym).getValue().toString()).toFloat() + equityVal

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting user failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
                // ...
            }
        }
        userRef.addValueEventListener(userListener)
    }
    fun setupLineChart(historyData:APIUtils.HistoryResponse){

        val entries = ArrayList<Entry>()
        for(i in 0 until 5){
            entries.add(Entry(i.toFloat(),historyData.prices[i]))
        }
        val linedata:LineDataSet = LineDataSet(entries, "Price")
        linedata.setColor(R.color.colorPrimary)
        chart.setData(LineData(linedata))
        chart.xAxis.granularity = 1f
        chart.xAxis.setValueFormatter(MyValueFormatter(historyData.labels))
        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart.axisRight.isEnabled = false
        chart.setDrawGridBackground(false)
        chart.setAutoScaleMinMaxEnabled(true)
        val desc = Description()
        desc.text = "Recent price history for" + sym
        chart.description = desc
        chart.invalidate()
    }

    inner class MyValueFormatter(val dates:ArrayList<String>) : IAxisValueFormatter {
        override fun getFormattedValue(value: Float, axis: AxisBase?): String {
            return dates[value.toInt()]
        }
    }
}
