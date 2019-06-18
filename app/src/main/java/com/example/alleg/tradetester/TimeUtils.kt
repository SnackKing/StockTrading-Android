package com.example.alleg.tradetester

import java.text.SimpleDateFormat
import java.util.*

object TimeUtils {

    val sdf = SimpleDateFormat("HH:mm:ss")
    val openTime = Calendar.getInstance()

    val closeTime = Calendar.getInstance()


    fun isMarketOpen(time: String = sdf.format(Date())):Boolean{
        openTime.time = sdf.parse("09:30:00")
        closeTime.time = sdf.parse("17:00:00")
        val current = Calendar.getInstance()
        current.time = sdf.parse(time)

        return current.time.before(closeTime.time) && current.time.after(openTime.time)
    }
}