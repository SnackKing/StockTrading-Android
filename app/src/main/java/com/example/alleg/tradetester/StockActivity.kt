package com.example.alleg.tradetester

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_stock.*
import kotlinx.android.synthetic.main.stock_content.view.*
import org.json.JSONObject
import java.lang.reflect.Field
import java.sql.Timestamp
import java.util.Date
import kotlin.collections.ArrayList

class StockActivity : BaseActivity() {
    lateinit var header:View
    private lateinit var sym:String
    private lateinit var stock:Stock
    private lateinit var auth: FirebaseAuth
    lateinit var database: DatabaseReference
    var balance = 0f
    var watchState = 0
    enum class Action{
        BUY,
        SELL
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stock)
        header = layoutInflater.inflate(R.layout.stock_content,null)
        news_items.addHeaderView(header)
        val mJsonObject = JSONObject(intent.getStringExtra("data"))
        stock = Stock(mJsonObject)
        sym = stock.symbol
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        setTextViews()
        header.watch_btn.setOnClickListener {
            //user was not watching this stock before. Start watching now
            if(watchState == 0){
                database.child("users").child(auth.uid).child("added").child(sym).setValue("")
                header.watch_btn.text = getString(R.string.stop_watch)
                watchState = 1
            }
            //user was watching this stock before. Stop watching
            else{
                database.child("users").child(auth.uid).child("added").child(sym).removeValue()
                header.watch_btn.text = getString(R.string.watch)
                watchState = 0
            }
        }
        header.buy.setOnClickListener { createDialogue(Action.BUY) }
        header.sell.setOnClickListener { createDialogue(Action.SELL) }

        Log.d("STOCK", "THIS WORKED")
        APIUtils.HistoryResponse(sym,this)
        APIUtils.makeNewsAPICall(sym,this)

    }
    fun setTextViews(){
        header.symbol.text =  String.format(resources.getString(R.string.symbol), stock.name, stock.symbol)
        header.price.text = String.format(resources.getString(R.string.price), stock.price.toString())
        header.change.text =  String.format(resources.getString(R.string.change_price), stock.change.toString())
        header. change_pct.text = String.format(resources.getString(R.string.change_pct), stock.change_pct.toString())
        header.todayHigh.text = String.format(resources.getString(R.string.today_high), stock.high.toString())
        header. todayLow.text =  String.format(resources.getString(R.string.today_low), stock.low.toString())
        header. yearHigh.text =  String.format(resources.getString(R.string.year_high), stock.yearHigh.toString())
        header.  yearLow.text =  String.format(resources.getString(R.string.year_low), stock.yearLow.toString())
        header. marketCap.text = String.format(resources.getString(R.string.market_cap), stock.market_cap)
        header. shares.text =  String.format(resources.getString(R.string.shares), stock.shares)
        header.  volume.text =  String.format(resources.getString(R.string.volume), stock.volume)
        header.  volume_avg.text =  String.format(resources.getString(R.string.volume_avg), stock.volume_avg)

        getUserStockData()

    }
    fun setOwnedTextViews(){
        header. numShares.text = String.format(resources.getString(R.string.shares_owned), stock.numOwned.toString())
        val equityVal = (stock.numOwned * stock.price)
        header.    equity.text = String.format(resources.getString(R.string.equity), equityVal)
        header.    currentReturn.text = String.format(resources.getString(R.string.curReturn), stock.curReturn + equityVal)
        header.   card_equity.visibility = View.VISIBLE
        header.    notOwnedText.visibility = View.GONE
    }
    fun setHistoryTextViews(){
        header.     totalTransactions.text =  String.format(resources.getString(R.string.numTrans), stock.numTransactions.toString())
        header.     totalReturn.text =  String.format(resources.getString(R.string.totalReturn), stock.totalReturn.toString())
        header.     card_history.visibility = View.VISIBLE
    }
    fun enableOrDisableButtons(){
        if(balance >= stock.price){
            enable(header.buy)
        }
        else{
            disable(header.buy)
        }
        if(stock.numOwned == 0){
            disable(header.sell)
        }
        else{
            enable(header.sell)
        }
    }
    private fun enable(button: Button){
        button.isEnabled = true
        button.setBackgroundColor(getColor(R.color.colorPrimary))
    }
    private fun disable(button:Button){
        button.isEnabled = false
        button.setBackgroundColor(Color.parseColor("#D3D3D3"))
    }

    fun getUserStockData(){
        val uid = auth.uid
        val userRef = database.child("users").child(uid)
        val userListener = object : ValueEventListener {
            override fun onDataChange(user: DataSnapshot) {
                balance = (user.child("balance").value.toString()).toFloat()
                if(user.hasChild("owned") && user.child("owned").hasChild(sym)){
                    stock.numOwned =  (user.child("owned").child(sym).value.toString()).toInt()
                    stock.curReturn = user.child("return").child(sym).value.toString().toFloat()
                    setOwnedTextViews()

                }
                if(user.hasChild("added") && user.child("added").hasChild(sym)){
                    header.   watch_btn.text = getString(R.string.stop_watch)
                    watchState = 1
                }
                if(user.hasChild("stats") && user.child("stats").child("transCount").hasChild(sym)){
                    stock.numTransactions = user.child("stats").child("transCount").child(sym).value.toString().toInt()
                    stock.totalReturn = user.child("stats").child("totalreturn").child(sym).value.toString().toFloat()
                    setHistoryTextViews()
                }
                enableOrDisableButtons()

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting user failed, log a message
                Log.w("STOCK", "loadPost:onCancelled", databaseError.toException())
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
        linedata.color = R.color.colorPrimary
        header.  chart.data = LineData(linedata)
        header.    chart.xAxis.granularity = 1f
        header.    chart.xAxis.valueFormatter = MyValueFormatter(historyData.labels)
        header.    chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        header.    chart.axisRight.isEnabled = false
        header.   chart.setDrawGridBackground(false)
        header.   chart.isAutoScaleMinMaxEnabled = true
        val desc = Description()
        desc.text = "Recent price history for $sym"
        header.   chart.description = desc
        header.   chart.invalidate()
    }
    fun setupNewsList(newsList:ArrayList<NewsItem>){
        val adapter = NewsListAdapter(this, newsList)
        news_items.adapter = adapter
         news_items.setOnItemClickListener { _, _, position, _ ->
             if(position != 0) {
                 val selectedArticle = newsList[position-1]
                 val intent = Intent(this, WebView::class.java)
                 intent.putExtra("url", selectedArticle.url)
                 startActivity(intent)
             }
         }
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
        numberPicker.maxValue = max
        numberPicker.minValue = 1
        setNumberPickerTextColor(numberPicker, R.color.black)
            val builder = AlertDialog.Builder(this,R.style.AlertDialogCustom)
            builder.apply {
                setPositiveButton(R.string.confirm,
                        DialogInterface.OnClickListener { dialog, id ->
                            // User clicked OK button
                            if(type == Action.BUY) {
                                val num = numberPicker.value
                                balance -= num*stock.price
                                database.child("users").child(auth.uid).child("balance").setValue(balance)
                                var ts = Timestamp(Date().time).toString()
                                ts = ts.substring(0,ts.indexOf('.'))
                                stock.numOwned += num
                                database.child("users").child(auth.uid).child("owned").child(sym).setValue(stock.numOwned)

                                stock.curReturn -= (num*stock.price)
                                database.child("users").child(auth.uid).child("return").child(sym).setValue(stock.curReturn)

                                database.child("users").child(auth.uid).child("orders").child("buy").child(ts).setValue(Order(num, stock.price, stock.symbol))

                                stock.numTransactions+= num
                                database.child("users").child(auth.uid).child("stats").child("transCount").child(sym).setValue(stock.numTransactions)

                                stock.totalReturn -= (num*stock.price)
                                database.child("users").child(auth.uid).child("stats").child("totalreturn").child(sym).setValue(stock.totalReturn)

                                val msg = "You bought " + num + " shares of " + sym
                                SnackBarMaker.makeSnackBar(context, root, msg, Snackbar.LENGTH_LONG).show()
                                setOwnedTextViews()
                                setHistoryTextViews()
                                enableOrDisableButtons()
                            }
                            else if(type == Action.SELL){
                                val num = numberPicker.value
                                balance += num*stock.price
                                database.child("users").child(auth.uid).child("balance").setValue(balance)
                                var ts = Timestamp(Date().time).toString()
                                ts = ts.substring(0,ts.indexOf('.'))
                                stock.numOwned = stock.numOwned - num
                                database.child("users").child(auth.uid).child("owned").child(sym).setValue(stock.numOwned)
                                //all shares sold, remove relevant info from database
                                if(stock.numOwned == 0){
                                    database.child("users").child(auth.uid).child("owned").child(sym).removeValue()
                                    database.child("users").child(auth.uid).child("return").child(sym).removeValue()
                                    header.            card_equity.visibility = View.GONE
                                }
                                else{
                                    setOwnedTextViews()
                                }
                                setHistoryTextViews()
                                enableOrDisableButtons()

                                stock.curReturn += (num*stock.price)
                                database.child("users").child(auth.uid).child("return").child(sym).setValue(stock.curReturn)

                                database.child("users").child(auth.uid).child("orders").child("sell").child(ts).setValue(Order(num, stock.price, stock.symbol))

                                stock.numTransactions += num
                                database.child("users").child(auth.uid).child("stats").child("transCount").child(sym).setValue(stock.numTransactions)

                                stock.totalReturn += num*stock.price
                                database.child("users").child(auth.uid).child("stats").child("totalreturn").child(sym).setValue(stock.totalReturn)
                                val msg = "You sold $num shares of $sym"
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
        val selectorWheelPaintField:Field = numberPicker.javaClass
            .getDeclaredField("mSelectorWheelPaint")
        selectorWheelPaintField.isAccessible = true
        (selectorWheelPaintField.get(numberPicker) as Paint).color = color
    }
    catch(e: NoSuchFieldException ){
        Log.w("setNumberPickerTextColor", e)
    }
    catch(e: IllegalAccessException){
        Log.w("setNumberPickerTextColor", e)
    }
    catch(e: IllegalArgumentException ){
        Log.w("setNumberPickerTextColor", e)
    }

    val count = numberPicker.childCount
    for(i in 0 until count){
        val child:View = numberPicker.getChildAt(i)
        (child as? EditText)?.setTextColor(color)
    }
    numberPicker.invalidate()
}

    inner class MyValueFormatter(private val dates:ArrayList<String>) : IAxisValueFormatter {
        override fun getFormattedValue(value: Float, axis: AxisBase?): String {
            return dates[value.toInt()]
        }
    }
}
