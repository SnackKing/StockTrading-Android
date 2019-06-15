package com.example.alleg.tradetester

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class HomeActivity : StockListFragment.OnListFragmentInteractionListener, WatchedFragment.OnListFragmentInteractionListener, AppCompatActivity() {

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
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.search -> {
            // User chose the "Settings" item, show the app settings UI...
            Toast.makeText(this, "Search", Toast.LENGTH_SHORT).show()
            true
        }

        R.id.account -> {
            // User chose the "Favorite" action, mark the current item
            // as a favorite...
            Toast.makeText(this, "account", Toast.LENGTH_SHORT).show()

            true
        }
        R.id.transactions -> {
            // User chose the "Favorite" action, mark the current item
            // as a favorite...
            Toast.makeText(this, "trans", Toast.LENGTH_SHORT).show()

            true
        }
        R.id.about -> {
            // User chose the "Favorite" action, mark the current item
            // as a favorite...
            Toast.makeText(this, "about", Toast.LENGTH_SHORT).show()
            true
        }
        R.id.signout -> {
            // User chose the "Favorite" action, mark the current item
            // as a favorite...
            Toast.makeText(this, "signout", Toast.LENGTH_SHORT).show()
            true
        }
        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

}
