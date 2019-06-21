package com.zachary.alleg.tradetester

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.news_item.view.*

class NewsListAdapter(private val context: Context, private val items:ArrayList<NewsItem>): BaseAdapter() {
    override fun getCount(): Int {
        return items.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItem(position: Int): Any {
        return items[position]
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rowView = inflater.inflate(R.layout.news_item, parent, false)
        val curItem: NewsItem = getItem(position) as NewsItem
        rowView.title.text = curItem.title
        Picasso.get().load(curItem.imageUrl).into(rowView.image)
        rowView.source.text = curItem.sourceName
        rowView.content.text = curItem.text
        rowView.date.text = curItem.date
        rowView.sentiment.text = curItem.sentiment
        if(curItem.sentiment == "Positive"){
            rowView.sentiment.setTextColor(context.resources.getColor(R.color.green))
        }
        else if(curItem.sentiment == "Negative"){
            rowView.sentiment.setTextColor(context.resources.getColor(R.color.red))
        }


        return rowView
    }
}