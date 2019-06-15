package com.example.alleg.tradetester


import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.alleg.tradetester.StockListFragment.OnListFragmentInteractionListener
import kotlinx.android.synthetic.main.owned_header.view.*
import kotlinx.android.synthetic.main.transaction_item.view.*


class TransactionListAdapter(
        var mValues: List<Transaction>, var context:Context) : RecyclerView.Adapter<TransactionListAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionListAdapter.ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.transaction_item, parent, false)
            return TransactionListAdapter.ViewHolder(view)

    }

    override fun onBindViewHolder(holder: TransactionListAdapter.ViewHolder, position: Int) {

            val item = getItem(position)
            holder.dateView.text =  String.format(context.resources.getString(R.string.boughtDate), item.date)
            holder.symbolView.text = item.symbol
            holder.priceView.text = String.format(context.resources.getString(R.string.pricePerShare), item.price.toString())
            holder.shareView.text = String.format(context.resources.getString(R.string.transShares), item.numShares.toString())
    }



    private fun getItem(position: Int): Transaction {
        return mValues.get(position)
    }

    override fun getItemCount(): Int = mValues.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var dateView = itemView.date
        var symbolView = itemView.symbol
        var shareView = itemView.shareCount
        var priceView = itemView.price

    }

}
