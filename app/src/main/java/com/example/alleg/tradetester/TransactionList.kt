package com.example.alleg.tradetester

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

class TransactionList : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    lateinit var database: DatabaseReference
    var buys = arrayListOf<Transaction>()
    var sells = arrayListOf<Transaction>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_list)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        transTabs.addTab(transTabs.newTab().setText("Buy Orders"))
        transTabs.addTab(transTabs.newTab().setText("Sell Orders"))
        transTabs.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab:TabLayout.Tab) {
                Toast.makeText(applicationContext, ("Tab is " + transTabs.selectedTabPosition), Toast.LENGTH_SHORT).show()

            }
            override fun onTabUnselected(tab:TabLayout.Tab) {
            }
            override fun onTabReselected(tab:TabLayout.Tab) {
            }
        })
        transList.layoutManager = LinearLayoutManager(this);
        getTransactions()


    }

    fun getTransactions(){
        val userReference = FirebaseDatabase.getInstance().reference.child("users").child(auth.uid).child("orders")
        val orderListener = object :ValueEventListener{

            override fun onDataChange(data: DataSnapshot) {
                if(data.hasChild("buy")){
                    data.child("buy").children.forEach{
                        val price = it.child("price").value.toString().toFloat()
                        val symbol = it.child("price").value.toString()
                        val numShares = it.child("numShares").value.toString().toInt()
                        buys.add(Transaction(it.key,symbol, numShares,price))
                    }
                   var adapter = TransactionListAdapter(buys)
                    transList.adapter = adapter
                }
            }
            override fun onCancelled(p0: DatabaseError?) {
            }


        }
        userReference.addListenerForSingleValueEvent(orderListener)


    }
}
