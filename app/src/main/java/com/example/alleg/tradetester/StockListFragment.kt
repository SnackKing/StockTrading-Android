package com.example.alleg.tradetester

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.alleg.tradetester.dummy.DummyContent
import com.example.alleg.tradetester.dummy.DummyContent.DummyItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import android.support.v7.widget.DefaultItemAnimator



/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [StockListFragment.OnListFragmentInteractionListener] interface.
 */
class StockListFragment : Fragment() {

    val TAG:String = "STOCKLISTFRAG"
    private var columnCount = 1
    lateinit var auth: FirebaseAuth
    lateinit var database: DatabaseReference
    lateinit var uid:String
    private var listener: OnListFragmentInteractionListener? = null
    private lateinit var ownedView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser?.uid ?: ""
        database = FirebaseDatabase.getInstance().reference
        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_item_list, container, false)
        ownedView = view.findViewById<RecyclerView>(R.id.ownedList)
        ownedView.setLayoutManager(LinearLayoutManager(getActivity()));
        readDataAndCreateAdapter()
        ownedView.setItemAnimator(DefaultItemAnimator())



        return view
    }

    fun readDataAndCreateAdapter(){
        val userReference = FirebaseDatabase.getInstance().reference.child("users").child(uid).child("owned")
        val stockListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    val ownedList = arrayListOf<Stock>()
                    val children = dataSnapshot.children
                    children.forEach{
                        var curStock = Stock(symbol = it.key, price = 10f, change = 0.1f)
                        ownedList.add(curStock)
                    }

                    ownedView.adapter = MyItemRecyclerViewAdapter(ownedList, listener)

                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
                // ...
            }
        }
        userReference.addValueEventListener(stockListener)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson
     * [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onListFragmentInteraction(item: Stock)
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
                StockListFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_COLUMN_COUNT, columnCount)
                    }
                }
    }
}
