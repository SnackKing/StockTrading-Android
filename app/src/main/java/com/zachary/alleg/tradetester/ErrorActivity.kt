package com.zachary.alleg.tradetester

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_error.*

class ErrorActivity : BaseActivity() {

    enum class ErrorCodes{
        NO_STOCK_FOUND,
        SOMETHING_WENT_WRONG,
        ERROR_READING_DATA
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
        else if(code == ErrorCodes.ERROR_READING_DATA){
            return getString(R.string.error_reading_data)
        }
        else{
            return getString(R.string.error_placeholder)
        }
    }
}
