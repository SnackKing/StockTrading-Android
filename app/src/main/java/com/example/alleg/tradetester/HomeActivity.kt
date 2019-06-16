package com.example.alleg.tradetester

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class HomeActivity : StockListFragment.OnListFragmentInteractionListener, WatchedFragment.OnListFragmentInteractionListener, BaseActivity() {

     lateinit var auth: FirebaseAuth
     lateinit var database: DatabaseReference



    override fun onListFragmentInteraction(item: Stock) {
        val name = item.symbol
        APIUtils.makeAPICallForSingleStock(name,this)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        setContentView(R.layout.activity_home)
    }


}
