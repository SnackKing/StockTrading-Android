package com.zachary.alleg.tradetester

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_login.*
import org.w3c.dom.Text
import android.R.id.edit
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.preference.PreferenceManager
import org.jetbrains.anko.doAsync


/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : AppCompatActivity() {
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private lateinit var auth: FirebaseAuth
    private val TAG = "LOGIN"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        title = getString(R.string.action_sign_in_short)
        auth = FirebaseAuth.getInstance()
        if(auth.currentUser != null){
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
        // Set up the login form.
        email_sign_in_button.setOnClickListener { attemptLogin() }
        go_sign_up.setOnClickListener { startActivity(Intent(this, SignupActivity::class.java)) }
    }



    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private fun attemptLogin() {
        // Store values at the time of the login attempt.
        var valid = true
        val emailStr = email.text.toString()
        val passwordStr = password.text.toString()
        if(TextUtils.isEmpty(emailStr)){
            valid = false
            email.error = getString(R.string.error_field_required)
        }
        if(TextUtils.isEmpty(passwordStr)){
            valid = false
            password.error = getString(R.string.error_field_required)
        }
        if(valid) firebaseLogin(emailStr, passwordStr)
    }

    private fun firebaseLogin(email:String, password:String){
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success")
                        val user = auth.currentUser
                        Toast.makeText(baseContext, "Authentication Successful.",
                                Toast.LENGTH_SHORT).show()
                        doAsync {
                            checkIfAfterMarketTradingEnabled(user?.uid)
                        }
                        startActivity(Intent(this, HomeActivity::class.java))
                        finish()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                    }
                }

    }
    private fun checkIfAfterMarketTradingEnabled(uid:String?){
            val ref = FirebaseDatabase.getInstance().reference.child("users").child(uid).child("afterHoursAllowed")
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(data: DataSnapshot) {
                    if (data.value == null) putInSharedPreferences(false)
                    else {
                        val afterHours: Boolean = data.value as Boolean
                        putInSharedPreferences(afterHours)

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    putInSharedPreferences(false)
                }


            })
    }
    fun putInSharedPreferences(value:Boolean){
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = preferences.edit()
        editor.putBoolean("afterHours", value)
        editor.apply()
    }


}
