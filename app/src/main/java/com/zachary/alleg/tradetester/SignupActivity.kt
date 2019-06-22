package com.zachary.alleg.tradetester

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_signup.*
import org.jetbrains.anko.doAsync
import java.util.regex.Pattern

/**
 * A login screen that offers login via email/password.
 */
class SignupActivity : AppCompatActivity() {
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private lateinit var auth: FirebaseAuth
    private val TAG = "LOGIN"
    private var codes = hashMapOf<String,String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        auth = FirebaseAuth.getInstance()
        // Set up the login form.
        email_sign_up_button.setOnClickListener { attemptLogin() }
        go_sign_in.setOnClickListener { startActivity(Intent(this, LoginActivity::class.java)) }

    }

    override fun onResume() {
        super.onResume()
        getCodes()
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private fun attemptLogin() {

        // Reset errors.
        email.error = null
        password.error = null

        // Store values at the time of the login attempt.
        val emailStr = email.text.toString()
        val passwordStr = password.text.toString()
        val nameStr = name.text.toString()
        val codeStr = joincode.text.toString()

        var cancel = false
        var focusView: View? = null

        if(TextUtils.isEmpty(nameStr)){
            name.error = getString(R.string.error_field_required)
            cancel = true
        }
        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(passwordStr) || (!isPasswordValid(passwordStr))) {
            password.error = getString(R.string.error_invalid_password)
            focusView = password
            cancel = true
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(emailStr)) {
            email.error = getString(R.string.error_field_required)
            focusView = email
            cancel = true
        } else if (!isEmailValid(emailStr)) {
            email.error = getString(R.string.error_invalid_email)
            focusView = email
            cancel = true
        }
        if(!TextUtils.isEmpty(codeStr)){
            if(!codes.containsKey(codeStr)){
                joincode.error = getString(R.string.code_error)
                focusView = joincode
                cancel = true
            }
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView?.requestFocus()
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            firebaseLogin(nameStr, emailStr, passwordStr, codeStr)

        }
    }

    private fun firebaseLogin(name: String, email:String, password:String, code:String){

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val uid = FirebaseAuth.getInstance().uid
                        val ref = FirebaseDatabase.getInstance().reference.child("users").child(uid)
                        ref.child("name").setValue(name)
                        ref.child("email").setValue(email)
                        if(TextUtils.isEmpty(code)){
                            ref.child("balance").setValue(500)

                        }
                        else{
                            addToClass(uid, code,name)
                        }
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success")
                        val user = auth.currentUser
                        Toast.makeText(baseContext, "Authentication Successful.",
                                Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, HomeActivity::class.java))
                        finish()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                    }
                    // ...
                }

    }
    private fun isEmailValid(email: String): Boolean {
        val regex = "^(.+)@(.+)$"
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length > 4
    }

    private fun getCodes(){
        doAsync {
            val ref = FirebaseDatabase.getInstance().reference.child("codes_tid")
            ref.addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(pairs: DataSnapshot) {
                    pairs.children.forEach{
                        codes.put(it.key, it.value.toString())
                    }
                }

                override fun onCancelled(p0: DatabaseError?) {
                }


            })
        }
    }
    fun addToClass(uid:String?, code:String, name:String){
        doAsync {
            val ref = FirebaseDatabase.getInstance().reference.child("teachers").child(codes[code]).child("classes").child(code).child("startingMoney")
            ref.addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onDataChange(data: DataSnapshot) {
                    FirebaseDatabase.getInstance().reference.child("users").child(uid).child("balance").setValue(data.value.toString().toInt())
                    FirebaseDatabase.getInstance().reference.child("teachers").child(codes[code]).child("classes").child(code).child("students").child(uid).setValue(name)
                }

                override fun onCancelled(p0: DatabaseError?) {
                }

            })
        }
    }









}
