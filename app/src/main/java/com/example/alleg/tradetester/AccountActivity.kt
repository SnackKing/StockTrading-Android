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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)
        readData()
    }

    fun readData(){
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().reference
        ref.child("users").child(uid).addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(data: DataSnapshot) {
                name.text = data.child("name").value.toString()
                email.text = data.child("email").value.toString()
                balance.text = "$" + (Math.round(data.child("balance").getValue().toString().toDouble() *100)/100).toString()
                if(data.hasChild("owned")){
                    val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    params.setMargins(5,5,0,5);

                    data.child("owned").children.forEach{
                        portfolio[it.key] = it.value.toString()
                        val newText = TextView(applicationContext)
                        newText.setText(String.format(resources.getString(R.string.portfolioItem),it.key,portfolio[it.key]))
                        newText.layoutParams = params
                        portfolioView.addView(newText)
                    }
                }

            }

            override fun onCancelled(p0: DatabaseError?) {
                val errorIntent = Intent(applicationContext, ErrorActivity::class.java)
                errorIntent.putExtra("code", ErrorActivity.ErrorCodes.ERROR_READING_DATA)
            }

        })
    }
}
