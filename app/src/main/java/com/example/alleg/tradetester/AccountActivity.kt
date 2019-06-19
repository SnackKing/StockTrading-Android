package com.example.alleg.tradetester

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_account.*
import android.view.ViewGroup.LayoutParams.FILL_PARENT
import android.widget.LinearLayout





class AccountActivity : BaseActivity() {
    var portfolio = hashMapOf<String,String>()
    var balanceVal = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)
        readData()
    }

    private fun readData(){
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().reference
        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        val context = this
        params.setMargins(5,5,0,5)
        ref.child("users").child(uid).addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(data: DataSnapshot) {
                name.text = data.child("name").value.toString()
                email.text = data.child("email").value.toString()
                balanceVal = ("%.2f".format(data.child("balance").value.toString().toDouble())).toDouble()
                balance.text = "$" + (balanceVal.toString())
                balanceAssets.text = "Balance: $" + (balanceVal.toString())
                total.text = "Total: $" + "%.2f".format(balanceVal)
                total
                if(data.hasChild("owned")){
                    var paramString:StringBuilder = StringBuilder()
                    data.child("owned").children.forEach{
                        paramString.append(it.key)
                        paramString.append(',')
                        portfolio[it.key] = it.value.toString()
                        val newText = TextView(applicationContext)
                        newText.text = String.format(resources.getString(R.string.portfolioItem),it.key,portfolio[it.key])
                        newText.layoutParams = params
                        portfolioView.addView(newText)
                    }
                    if(paramString.length > 0) paramString.removeSuffix(",")
                    APIUtils.getPricesForAllOwned(paramString.toString(),context)
                }

            }
            override fun onCancelled(p0: DatabaseError?) {
                val errorIntent = Intent(applicationContext, ErrorActivity::class.java)
                errorIntent.putExtra("code", ErrorActivity.ErrorCodes.ERROR_READING_DATA)
            }
        })
        ref.child("users").child(uid).child("stats").child("transCount").orderByValue().limitToLast(5).addListenerForSingleValueEvent(object : ValueEventListener  {
            override fun onDataChange(data: DataSnapshot?) {
                if(data != null) {
                    //add all views into an arraylist so they can be easily reversed and added to the view in the correct order
                    val viewArray = arrayListOf<TextView>()
                    data.children.forEach {
                        val newText = TextView(applicationContext)
                        newText.text = String.format(resources.getString(R.string.favoriteItem),it.key,it.value.toString())
                        newText.layoutParams = params
                        viewArray.add(newText)
                    }
                    viewArray.reverse()
                    viewArray.forEach {
                        favoritesView.addView(it)
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError?) {
            }

        })
    }
    fun populateAssets(priceMap:HashMap<String,Double>){
        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        params.setMargins(5,5,0,5)
        var totalVal = 0.0
        priceMap.forEach{
            val price = it.value
            val num = portfolio[it.key]?.toInt() ?: 0
            val eq = price *num
            val newText = TextView(this)
            val text = String.format(getString(R.string.assetItem),it.key, ("%.2f".format(eq)))
            newText.text = text
            newText.layoutParams = params
            assetsView.addView(newText)
            totalVal += eq
        }
        total.text = "Total: $" + "%.2f".format(totalVal + balanceVal)
        balanceAssets.text = "Balance: $" + balanceVal.toString()

    }
}
