package com.example.alleg.tradetester

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_error.*

class ErrorActivity : AppCompatActivity() {

    public enum class ErrorCodes{
        NO_STOCK_FOUND,
        SOMETHING_WENT_WRONG
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_error)
        val code = intent.getSerializableExtra("code") as ErrorCodes
        errorMessage.text = getMessage(code)
    }
    private fun getMessage(code:ErrorCodes):String{
        if(code == ErrorCodes.NO_STOCK_FOUND){
            return getString(R.string.no_stock_message)
        }
        else{
            return getString(R.string.error_placeholder)
        }
    }
}
