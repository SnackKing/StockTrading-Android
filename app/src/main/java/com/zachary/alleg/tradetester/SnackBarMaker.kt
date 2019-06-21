package com.zachary.alleg.tradetester

import android.content.Context
import android.support.design.widget.Snackbar
import android.view.View
import android.widget.TextView

object SnackBarMaker {

    fun makeSnackBar(context: Context, view: View, msg:String, length:Int):Snackbar{
        val snackbar = Snackbar.make(view, msg, length)
        snackbar.view.setBackgroundColor(context.resources.getColor(R.color.colorPrimary))
        var textView = snackbar.view.findViewById(android.support.design.R.id.snackbar_text) as TextView
        textView.setTextColor(context.resources.getColor(R.color.white))
        return snackbar
    }

}