package com.zachary.alleg.tradetester

import android.app.PendingIntent.getActivity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v7.widget.LinearLayoutManager
import android.widget.LinearLayout
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_transaction_list.*
import kotlinx.android.synthetic.main.stock_content.*

class TransactionList : BaseActivity() {

    lateinit var auth: FirebaseAuth
    lateinit var database: DatabaseReference
    var buys = arrayListOf<Transaction>()
    var sells = arrayListOf<Transaction>()
    lateinit var adapter:TransactionListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_list)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        //just initializing this to prevent null pointers
        adapter = TransactionListAdapter(buys,this)
        transTabs.addTab(transTabs.newTab().setText("Buy Orders"))
        transTabs.addTab(transTabs.newTab().setText("Sell Orders"))
        transTabs.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab:TabLayout.Tab) {
                    if (tab.position == 0) {
                        adapter.mValues = buys
                    } else {
                        adapter.mValues = sells
                    }
                    adapter.notifyDataSetChanged()


            }
            override fun onTabUnselected(tab:TabLayout.Tab) {
            }
            override fun onTabReselected(tab:TabLayout.Tab) {
            }
        })
        transList.layoutManager = LinearLayoutManager(this)
        getBuyOrders()
        getSellOrders()


    }

    fun getBuyOrders(){
        val userReference = FirebaseDatabase.getInstance().reference.child("users").child(auth.uid).child("orders").child("buy").orderByKey().limitToLast(100)
        val orderListener = object :ValueEventListener{

            override fun onDataChange(data: DataSnapshot) {
                    data.children.forEach{
                        val price = it.child("price").value.toString().toFloat()
                        val symbol = it.child("symbol").value.toString()
                        val numShares = it.child("numShares").value.toString().toInt()
                        buys.add(Transaction(it.key,symbol, numShares,price))
                    }
                    buys.reverse()
                    adapter = TransactionListAdapter(buys,applicationContext)
                    transList.adapter = adapter


            }
            override fun onCancelled(p0: DatabaseError?) {
            }


        }
        userReference.addListenerForSingleValueEvent(orderListener)


    }
    fun getSellOrders(){
        val userReference = FirebaseDatabase.getInstance().reference.child("users").child(auth.uid).child("orders").child("sell").orderByKey().limitToLast(100)
        val orderListener = object :ValueEventListener{

            override fun onDataChange(data: DataSnapshot) {
                data.children.forEach{
                    val price = it.child("price").value.toString().toFloat()
                    val symbol = it.child("symbol").value.toString()
                    val numShares = it.child("numShares").value.toString().toInt()
                    sells.add(Transaction(it.key,symbol, numShares,price))
                }
                sells.reverse()
               adapter.notifyDataSetChanged()
            }
            override fun onCancelled(p0: DatabaseError?) {
            }


        }
        userReference.addListenerForSingleValueEvent(orderListener)


    }
}
