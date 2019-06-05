package com.example.alleg.tradetester

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.alleg.tradetester.dummy.DummyContent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class HomeActivity : StockListFragment.OnListFragmentInteractionListener, WatchedFragment.OnListFragmentInteractionListener, AppCompatActivity() {

    lateinit var auth: FirebaseAuth
     lateinit var database: DatabaseReference



    override fun onListFragmentInteraction(item: Stock) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        setContentView(R.layout.activity_home)
    }

}
