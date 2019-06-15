package com.example.alleg.tradetester

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_transaction_list.*

class TransactionList : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_list)
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


    }
}
