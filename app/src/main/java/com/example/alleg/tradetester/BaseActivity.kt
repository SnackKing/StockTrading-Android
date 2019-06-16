package com.example.alleg.tradetester

import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

open class BaseActivity: AppCompatActivity() {
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);   //show back button
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
            startActivity(Intent(this, TransactionList::class.java))
            true
        }
        R.id.about -> {
            // User chose the "Favorite" action, mark the current item
            // as a favorite...
            Toast.makeText(this, "about", Toast.LENGTH_SHORT).show()
            true
        }
        R.id.signout -> {
            //log user out and navigate to
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            true
        }
        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true

    }
}