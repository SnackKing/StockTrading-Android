package com.example.alleg.tradetester

import android.app.AlertDialog
import android.app.PendingIntent.getActivity
import android.content.DialogInterface
import android.graphics.Paint
import android.nfc.Tag
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.TextView
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
import java.lang.reflect.Field
import java.text.DecimalFormat
import java.util.Date;
import java.sql.Timestamp;

class StockActivity : AppCompatActivity() {
    private lateinit var sym:String
    private lateinit var stock:Stock
    lateinit var auth: FirebaseAuth
    lateinit var database: DatabaseReference
    var balance = 0f
    val TAG = "STOCKPAGE"
    var watchState = 0
    var transCount = 0
    var totalReturn = 0f
    var curReturn = 0f
    enum class Action{
        BUY,
        SELL
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stock)
        val mJsonObject = JSONObject(intent.getStringExtra("data"))
        stock = Stock(mJsonObject)
        sym = stock.symbol
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        setTextViews()
        watch_btn.setOnClickListener(View.OnClickListener {
            //user was not watching this stock before. Start watching now
            if(watchState == 0){
                database.child("users").child(auth.uid).child("added").child(sym).setValue("")
                watch_btn.text = getString(R.string.stop_watch)
                watchState = 1
            }
            //user was watching this stock before. Stop watching
            else{
                database.child("users").child(auth.uid).child("added").child(sym).removeValue()
                watch_btn.text = getString(R.string.watch)
                watchState = 0
            }
        })
        buy.setOnClickListener(View.OnClickListener { createDialogue(Action.BUY) })
        sell.setOnClickListener(View.OnClickListener { createDialogue(Action.SELL) })

        Log.d("STOCK", "THIS WORKED")
        APIUtils.HistoryResponse(sym,this)

    }
    fun setTextViews(){
        symbol.text = stock.name + " (" + stock.symbol + ")"
        price.text = stock.price.toString() + " USD"
        change.text = "Today's Change: " +  stock.change.toString() + " USD"
        change_pct.text = "Change in %: " + stock.change_pct.toString() + " USD"
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
                balance = (user.child("balance").value.toString()).toFloat()
                if(user.hasChild("owned") && user.child("owned").hasChild(sym)){
                    stock.numOwned =  (user.child("owned").child(sym).value.toString()).toInt()
                    numShares.text = "Shares Owned: "  + stock.numOwned
                    val equityVal = (stock.numOwned * stock.price)
                    equity.text = "Total Equity: " + equityVal
                    if (stock.numOwned != 0)currentReturn.text = "Return: " + (user.child("return").child(sym).getValue().toString()).toFloat() + equityVal
                    curReturn = user.child("return").child(sym).getValue().toString().toFloat()
                }
                if(user.hasChild("added") && user.child("added").hasChild(sym)){
                    watch_btn.text = getString(R.string.stop_watch)
                    watchState = 1
                }
                if(user.hasChild("stats") && user.child("stats").child("transCount").hasChild(sym)){
                    transCount = user.child("stats").child("transCount").child(sym).getValue().toString().toInt()
                    totalReturn = user.child("stats").child("totalreturn").child(sym).getValue().toString().toFloat()

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting user failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
                // ...
            }
        }
        userRef.addListenerForSingleValueEvent(userListener)
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
    private fun createDialogue(type:Action){
        var numberPicker = NumberPicker(this)
        var max:Int = 0
        var title = ""
        if(type == Action.BUY){
            max = (balance/stock.price).toInt()
            title = getString(R.string.dialogueTitle_buy)
        }
        else{
            max = stock.numOwned
            title = getString(R.string.dialogueTitle_sell)

        }
        numberPicker.setMaxValue(max);
        numberPicker.setMinValue(1);
        setNumberPickerTextColor(numberPicker, R.color.black)
            val builder = AlertDialog.Builder(this,R.style.AlertDialogCustom)
            builder.apply {
                setPositiveButton(R.string.confirm,
                        DialogInterface.OnClickListener { dialog, id ->
                            // User clicked OK button
                            if(type == Action.BUY) {
                                val num = numberPicker.value
                                database.child("users").child(auth.uid).child("balance").setValue(balance - (num*stock.price))
                                var ts = Timestamp(Date().time).toString()
                                ts = ts.substring(0,ts.indexOf('.'))
                                database.child("users").child(auth.uid).child("owned").child(sym).setValue(stock.numOwned + num)
                                database.child("users").child(auth.uid).child("return").child(sym).setValue(curReturn - (num*stock.price))
                                database.child("users").child(auth.uid).child("orders").child("buy").child(ts).setValue(Order(num, stock.price, stock.symbol))
                                database.child("users").child(auth.uid).child("stats").child("transCount").child(sym).setValue(transCount + num)
                                database.child("users").child(auth.uid).child("stats").child("totalreturn").child(sym).setValue(totalReturn - (num*stock.price))

                                val msg = "You bought " + num + " shares of " + sym
                                SnackBarMaker.makeSnackBar(context, root, msg, Snackbar.LENGTH_LONG).show()
                            }
                            else if(type == Action.SELL){
                                val num = numberPicker.value

                                database.child("users").child(auth.uid).child("balance").setValue(balance - (num*stock.price))
                                var ts = Timestamp(Date().time).toString()
                                ts = ts.substring(0,ts.indexOf('.'))
                                stock.numOwned = stock.numOwned - num
                                database.child("users").child(auth.uid).child("owned").child(sym).setValue(stock.numOwned)
                                //all shares sold, remove relevant info from database
                                if(stock.numOwned == 0){
                                    database.child("user").child(auth.uid).child("owned").child(sym).removeValue()
                                    database.child("user").child(auth.uid).child("return").child(sym).removeValue()
                                }
                                database.child("users").child(auth.uid).child("return").child(sym).setValue(curReturn + (num*stock.price))
                                database.child("users").child(auth.uid).child("orders").child("sell").child(ts).setValue(Order(num, stock.price, stock.symbol))
                                database.child("users").child(auth.uid).child("stats").child("transCount").child(sym).setValue(transCount + num)
                                database.child("users").child(auth.uid).child("stats").child("totalreturn").child(sym).setValue(totalReturn + (num*stock.price))
                                val msg = "You sold " + num + " shares of " + sym
                                SnackBarMaker.makeSnackBar(context, root, msg, Snackbar.LENGTH_LONG).show()
                            }

                        })
                setNegativeButton(R.string.cancel,
                        DialogInterface.OnClickListener { dialog, id ->
                            // User cancelled the dialog
                        })
            }
            builder.setView(numberPicker)
            builder.setTitle(title)

            // Create the AlertDialog
            builder.create().show()


    }
    fun setNumberPickerTextColor(numberPicker: NumberPicker, color:Int)
{

    try{
        var selectorWheelPaintField:Field = numberPicker.javaClass
            .getDeclaredField("mSelectorWheelPaint");
        selectorWheelPaintField.setAccessible(true);
        (selectorWheelPaintField.get(numberPicker) as Paint).setColor(color);
    }
    catch(e: NoSuchFieldException ){
        Log.w("setNumberPickerTextColor", e);
    }
    catch(e: IllegalAccessException){
        Log.w("setNumberPickerTextColor", e);
    }
    catch(e: IllegalArgumentException ){
        Log.w("setNumberPickerTextColor", e);
    }

    val count = numberPicker.getChildCount();
    for(i in 0 until count){
        var child:View = numberPicker.getChildAt(i);
        if(child is EditText){
            child.setTextColor(color)
        }
    }
    numberPicker.invalidate();
}

    inner class MyValueFormatter(val dates:ArrayList<String>) : IAxisValueFormatter {
        override fun getFormattedValue(value: Float, axis: AxisBase?): String {
            return dates[value.toInt()]
        }
    }
}
