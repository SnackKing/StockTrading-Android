package com.example.alleg.tradetester

import android.app.SearchManager
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import com.example.alleg.tradetester.dummy.DummyContent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import android.view.MenuInflater
import android.support.v4.view.MenuItemCompat.getActionView
import android.widget.SearchView


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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.options_menu, menu)
        // Associate searchable configuration with the SearchView
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.search).actionView as SearchView
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(componentName))

        return true
    }

}
