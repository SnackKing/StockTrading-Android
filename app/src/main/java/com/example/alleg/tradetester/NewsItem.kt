package com.example.alleg.tradetester

import org.json.JSONObject

class NewsItem(data:JSONObject){
    var title = ""
    var url = ""
    var imageUrl = ""
    var text = ""
    var sentiment = ""
    var sourceName = ""
    var date = ""
    init{
        title = data.getString("title")
        url = data.getString("news_url")
        imageUrl = data.getString("image_url")
        text = data.getString("text")
        sentiment = data.getString("sentiment")
        sourceName = data.getString("source_name")
        date = data.getString("date")

    }

}