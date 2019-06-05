package com.example.alleg.tradetester

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


import com.example.alleg.tradetester.WatchedFragment.OnListFragmentInteractionListener
import com.example.alleg.tradetester.dummy.DummyContent.DummyItem

import kotlinx.android.synthetic.main.watched_item.view.*

/**
 * [RecyclerView.Adapter] that can display a [DummyItem] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 * TODO: Replace the implementation with code for your data type.
 */
class WatchedRecyclerViewAdapter(
        private val mValues: List<Stock>,
        private val mListener: OnListFragmentInteractionListener?)
    : RecyclerView.Adapter<WatchedRecyclerViewAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as Stock
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onListFragmentInteraction(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.watched_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.mSymbolView.text = item.symbol
        holder.mNameView.text = item.name
        holder.mPriceView.text = (item.price).toString()
        holder.mChangeView.text = (item.change).toString()

        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mSymbolView: TextView = mView.symbol
        val mNameView: TextView = mView.companyName
        val mPriceView: TextView = mView.price
        val mChangeView: TextView = mView.change

        override fun toString(): String {
            return super.toString() + " '" + mSymbolView.text + "'"
        }
    }
}
