package com.example.alleg.tradetester


import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.alleg.tradetester.WatchedFragment.OnListFragmentInteractionListener
import kotlinx.android.synthetic.main.owned_header.view.*
import kotlinx.android.synthetic.main.watched_item.view.*


class WatchedRecyclerViewAdapter(
        private val mValues: List<Stock>,
        private val mListener: OnListFragmentInteractionListener?)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as Stock
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onListFragmentInteraction(item)
        }
    }
    private val TYPE_HEADER = 0
    private val TYPE_ITEM = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        if (viewType == TYPE_ITEM) {
            //inflate your layout and pass it to view holder
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.fragment_item, parent, false)
            return VHItem(view)
        } else if (viewType == TYPE_HEADER) {
            //inflate your layout and pass it to view holder
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.owned_header, parent, false)
            return VHHeader(view);
        }

        throw  RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is VHItem) {
            val item = mValues[position-1]
            holder.mSymbolView.text = item.symbol
            holder.mNameView.text = item.name
            holder.mPriceView.text = (item.price).toString()

            with(holder.itemView) {
                tag = item
                setOnClickListener(mOnClickListener)
            }
        }
        else if(holder is VHHeader) {
            holder.textview!!.text = "Stocks You're Watching"

        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (isPositionHeader(position)) TYPE_HEADER else TYPE_ITEM

    }
    private fun isPositionHeader(position: Int): Boolean {
        return position == 0
    }

    private fun getItem(position: Int): Stock {
        return mValues.get(position-1)
    }

    override fun getItemCount(): Int = mValues.size

    internal inner class VHItem(mView: View) : RecyclerView.ViewHolder(mView) {
        val mSymbolView: TextView = mView.symbol
        val mNameView: TextView = mView.companyName
        val mPriceView: TextView = mView.price

    }

    internal inner class VHHeader(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textview: TextView = itemView.owned_header
    }

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
