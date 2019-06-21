package com.zachary.alleg.tradetester

import android.app.Activity
import android.app.SearchManager
import android.content.Intent
import android.os.Bundle

class SearchResultsActivity: Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {

        if (Intent.ACTION_SEARCH == intent.action) {
            val query = intent.getStringExtra(SearchManager.QUERY)
            APIUtils.makeAPICallForSingleStock(query, this)
            finish() //prevent search activity from going on back stack
        }
    }

}