package com.example.alleg.tradetester

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_watched_list.*
import org.json.JSONArray
import org.json.JSONObject


/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [StockListFragment.OnListFragmentInteractionListener] interface.
 */
class WatchedFragment : Fragment() {

    val TAG:String = "STOCKLISTFRAG"
    private var columnCount = 1
    lateinit var auth: FirebaseAuth
    lateinit var database: DatabaseReference
    lateinit var uid:String
    private var listener: OnListFragmentInteractionListener? = null
    private lateinit var ownedView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private var index:Int = 0

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
        ownedView.isNestedScrollingEnabled = false
        ownedView.layoutManager = LinearLayoutManager(activity)
        readDataAndCreateAdapter()
        ownedView.itemAnimator = DefaultItemAnimator()



        return view
    }

    fun readDataAndCreateAdapter(){
        val userReference = FirebaseDatabase.getInstance().reference.child("users").child(uid).child("added")
        val stockListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.value != null){
                    val ownedList = arrayListOf<Stock>()
                    val children = dataSnapshot.children
                    var symbolGroup = arrayListOf<String>()
                    var symString = ""
                    var symbol_index = HashMap<String,Int>()
                    var localIndex = 0
                    children.forEach{
                        if(localIndex % 5 == 0 && localIndex != 0){
                            symString = symString.removeSuffix(",")
                            symbolGroup.add(symString)
                            symString = ""
                        }
                        var curStock = Stock(symbol = it.key, price = 10f, change = 0.1f, change_pct = 0.1f)
                        ownedList.add(curStock)
                        symString += it.key + ','
                        symbol_index[it.key] = localIndex
                        localIndex++
                    }
                    if(!symString.equals("")) symbolGroup.add(symString.removeSuffix(","))
                    addPrices(ownedList, symbolGroup,symbol_index)
                    ownedView.adapter = WatchedRecyclerViewAdapter(ownedList, listener)


                }

            }
            fun addPrices(ownedList: List<Stock>,symbolGroup:List<String> , symbol_index: Map<String,Int>){
                symbolGroup.forEach {
                    val queue = Volley.newRequestQueue(context)
                    var url = "https://www.worldtradingdata.com/api/v1/stock?"
                    url += "api_token=mkUwgwc7TADeShHuZO7D2RRbeLu1b9PNd6Ptey0LkIeRliCUjdLJJB9UE4UX"
                    url += "&symbol=" + it
                    val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
                            Response.Listener { response ->
                                var data:JSONArray = response.getJSONArray("data")
                                print(data)
                                for(i in 0..data.length()-1){
                                    val stock:JSONObject = data.getJSONObject(i)
                                    ownedList.get(symbol_index[stock.get("symbol")]!!).price = stock.get("price").toString().toFloat()
                                    ownedList.get(symbol_index[stock.get("symbol")]!!).name = stock.get("name").toString()
                                    try {
                                        ownedList.get(symbol_index[stock.get("symbol")]!!).change = stock.get("day_change").toString().toFloat()
                                        ownedList.get(symbol_index[stock.get("symbol")]!!).change_pct = stock.get("change_pct").toString().toFloat()
                                    }
                                    catch (e:NumberFormatException){
                                        ownedList.get(symbol_index[stock.get("symbol")]!!).change = 0f
                                        ownedList.get(symbol_index[stock.get("symbol")]!!).change_pct = 0f
                                    }

                                    index++
                                }
                                ownedView.adapter.notifyDataSetChanged()

                            },

                            Response.ErrorListener { error ->
                                // TODO: Handle error
                            }
                    )
                    val st = jsonObjectRequest.toString()
                    queue.add(jsonObjectRequest)
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
